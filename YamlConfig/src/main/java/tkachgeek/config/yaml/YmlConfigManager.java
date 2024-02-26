package tkachgeek.config.yaml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import tkachgeek.config.Description;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Reloadable;
import tkachgeek.config.base.Utils;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.MessageArr;
import tkachgeek.config.minilocale.translatable.TranslatableMessage;
import tkachgeek.config.yaml.module.*;
import tkachgeek.tkachutils.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class YmlConfigManager {
  public HashMap<String, Config> configs = new HashMap<>();
  JavaPlugin plugin;
  ObjectMapper mapper;
  Logger logger;
  
  public YmlConfigManager(JavaPlugin plugin) {
    this(plugin, 50 * 1024 * 1024);
  }
  
  public YmlConfigManager(JavaPlugin plugin, int maxConfigSizeBytes) {
    this.plugin = plugin;
    this.logger = plugin.getLogger();
    
    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setCodePointLimit(maxConfigSizeBytes);
    
    YAMLFactory yaml = YAMLFactory.builder()
                                  .disable(YAMLGenerator.Feature.SPLIT_LINES)
                                  .loaderOptions(loaderOptions)
                                  .build();
    
    mapper = new ObjectMapper(yaml);
    
    mapper.findAndRegisterModules();
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    
    SimpleModule module = new SimpleModule("TkachGeekModules");
    
    module.addDeserializer(Location.class, new LocationDeserializer());
    module.addSerializer(Location.class, new LocationSerializer());
    
    module.addDeserializer(ItemStack.class, new ItemStackDeserializer());
    module.addSerializer(ItemStack.class, new ItemStackSerializer());
    
    module.addDeserializer(OfflinePlayer.class, new OfflinePlayerDeserializer());
    module.addSerializer(OfflinePlayer.class, new OfflinePlayerSerializer());
    
    module.addDeserializer(TranslatableMessage.class, new TranslatableMessageDeserializer());
    module.addSerializer(TranslatableMessage.class, new TranslatableMessageSerializer());
    
    module.addDeserializer(Message.class, new MessageDeserializer());
    module.addSerializer(Message.class, new MessageSerializer());
    
    module.addDeserializer(MessageArr.class, new MessageArrDeserializer());
    module.addSerializer(MessageArr.class, new MessageArrSerializer());
    
    module(module);
  }
  
  public void module(Module module) {
    mapper.registerModule(module);
  }
  
  public ObjectMapper getMapper() {
    return mapper;
  }
  
  public <T extends YmlConfig> T load(String path, Class<T> type) {
    long startTime = System.currentTimeMillis();
    boolean shouldSaveCopy = false;
    
    logger.info("");
    logger.info("Чтение конфига " + path + ".yml");
    
    T config = null;
    String yaml = "";
    
    try {
      yaml = Utils.readString(getPath(path));
      
      if (yaml.length() == 0) {
        logger.info("Файл не найден, будет создан дефолтный");
        config = Utils.getNewInstance(type);
        shouldSaveCopy = true;
      } else {
        config = mapper.readValue(yaml, type);
      }
    } catch (IOException e) {
      logger.warning("Не удалось прочесть конфиг " + path + ".yml");
      if (yaml.length() != 0) {
        String newPath = path + " " + new Timestamp(System.currentTimeMillis()).toString().replace(":", "-");
        logger.warning("Файл не пустой, создана копия под именем " + newPath + ".yml");
        Utils.writeString(getPath(newPath), yaml);
      }
      e.printStackTrace();
    }
    
    if (config == null) {
      logger.warning("Создание конфига " + path + ".yml");
      config = Utils.getNewInstance(type);
      shouldSaveCopy = true;
    }
    
    if (config == null) {
      logger.warning("Не удалось создать конфиг " + path + ".yml (" + type.getSimpleName() + ")");
    } else {
      config.path = path;
      config.setManager(this);
      
      configs.put(path, config);
      
      if (shouldSaveCopy) config.store();
      
      long elapsed = System.currentTimeMillis() - startTime;
      logger.info("Успешно загружен конфиг " + path + ".yml (заняло " + elapsed + "ms)");
    }
    
    return config;
  }
  
  public void store(String path, YmlConfig object, boolean async) {
    if (async) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> store(path, object));
    } else {
      store(path, object);
    }
  }
  
  public void store(String path, YmlConfig object) {
    StringWriter writer = new StringWriter();
    
    try {
      mapper.writeValue(writer, object);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    String serialized = writer.toString();
    
    String[] header = object.header();
    if (header != null) {
      serialized = CollectionUtils.toString(header, "#", "\n", false) + serialized;
    }
    
    for (var descriptionEntry : Arrays.stream(object.getClass().getDeclaredFields())
                                      .filter(x -> x.isAnnotationPresent(Description.class))
                                      .collect(Collectors.toMap(Field::getName, o -> o.getAnnotation(Description.class).value()))
                                      .entrySet()) {
      
      serialized = serialized.replaceFirst("(\n" + descriptionEntry.getKey() + ")",
                                           CollectionUtils.toString(descriptionEntry.getValue(), "\n#", "", false) + "$1");
    }
    
    Utils.writeString(getPath(path), serialized);
  }
  
  public void storeAll() {
    storeAll(false);
  }
  
  public void storeAll(boolean silent) {
    for (Config config : configs.values()) {
      long start = System.currentTimeMillis();
      if (config.storeAllEnabled) {
        if (!silent) logger.info("");
        if (!silent) logger.info("Сохранение конфига " + config.path + ".yml");
        
        try {
          config.store();
        } catch (Exception e) {
          
          logger.warning("Ошибка при сохранении конфига" + config.path + ".yml");
          
          e.printStackTrace();
          continue;
        }
        long elapsed = System.currentTimeMillis() - start;
        if (!silent)
          logger.info("Конфиг " + config.path + ".yml сохранён (заняло " + elapsed + "ms)");
      }
    }
  }
  
  public void reloadAllReloadable() {
    for (Config config : configs.values()) {
      if (config instanceof Reloadable) {
        
        logger.info("Перезагрузка конфига " + config.path + ".yml");
        
        try {
          ((Reloadable) config).reload();
        } catch (Exception e) {
          logger.warning("Перезагрузка конфига " + config.path + ".yml не удалась: " + e.getMessage());
          continue;
        }
        logger.info("Перезагрузка конфига " + config.path + ".yml прошла успешно");
      }
    }
  }
  
  public String toString(YmlConfig config) {
    StringWriter writer = new StringWriter();
    
    try {
      mapper.writeValue(writer, config);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return writer.toString();
  }
  
  public List<Config> reloadByCommand(Audience out) {
    List<Config> reloaded = new ArrayList<>();
    
    for (Config config : configs.values()) {
      if (config instanceof Reloadable) {
        
        out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml"));
        
        try {
          ((Reloadable) config).reload();
          reloaded.add(config);
          
          out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml прошла успешно"));
        } catch (Exception e) {
          out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml не удалась: " + e.getMessage()));
        }
      }
    }
    return reloaded;
  }
  
  @Deprecated
  public void flush(String configName, Audience out) {
    Utils.writeString(getPath(configName), "");
    
    reloadByCommand(configName, out);
    
    out.sendMessage(Component.text("Файл " + configName + ".yml очищен"));
  }
  
  public @Nullable Config reloadByCommand(String name, Audience out) {
    Optional<Config> optConfig = getByName(name);
    if (optConfig.isEmpty()) {
      out.sendMessage(Component.text("Конфик с именем " + name + " не найден или ещё не был загружен"));
      return null;
    }
    
    Config config = optConfig.get();
    
    if (config instanceof Reloadable) {
      
      out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml"));
      
      try {
        ((Reloadable) config).reload();
        
        out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml прошла успешно"));
        
        return getByName(name).orElse(null); //потому что config - это старый инстанс
      } catch (Exception e) {
        out.sendMessage(Component.text("Перезагрузка конфига " + config.path + ".yml не удалась: " + e.getMessage()));
      }
    } else {
      out.sendMessage(Component.text("Конфиг " + name + " не может быть перезагружен"));
    }
    return null;
  }
  
  public Optional<Config> getByName(String name) {
    return Optional.ofNullable(configs.getOrDefault(name, null));
  }
  
  public void scheduleAutosave(int ticks, boolean async) {
    scheduleAutosave(ticks, async, true);
  }
  
  public void scheduleAutosave(int ticks, boolean async, boolean silent) {
    if (async) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, (silent ? this::autosaveSilent : this::autosave), ticks, ticks);
    } else {
      Bukkit.getScheduler().runTaskTimer(plugin, (silent ? this::autosaveSilent : this::autosave), ticks, ticks);
    }
  }
  
  Path getPath(String path) {
    return Paths.get(plugin.getDataFolder().toString() + File.separatorChar + path + ".yml");
  }
  
  private void autosaveSilent() {
    storeAll(true);
  }
  
  private void autosave() {
    logger.info("Автоматическое сохранение конфигов..");
    storeAll(true);
    logger.info("Всё сохранено");
  }
}
