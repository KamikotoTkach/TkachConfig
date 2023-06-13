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
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.LoaderOptions;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Reloadable;
import tkachgeek.config.base.Utils;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.MessageArr;
import tkachgeek.config.minilocale.translatable.TranslatableMessage;
import tkachgeek.config.yaml.module.*;
import tkachgeek.tkachutils.scheduler.Scheduler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public class YmlConfigManager {
  public HashMap<String, Config> configs = new HashMap<>();
  JavaPlugin plugin;
  ObjectMapper mapper;
  Logger logger;
  
  public YmlConfigManager(JavaPlugin plugin) {
    this(plugin, 10 * 1024 * 1024);
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
    
    logger.info("");
    logger.info("Чтение конфига " + path + ".yml");
    
    T config = null;
    String yaml = "";
    
    try {
      yaml = Utils.readString(getPath(path));
      
      if (yaml.length() == 0) {
        logger.info("Файл не найден, будет использован дефолтный");
        config = Utils.getNewInstance(type);
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
    }
    
    if (config == null) {
      logger.warning("Не удалось создать конфиг " + path + ".yml (" + type.getSimpleName() + ")");
    } else {
      config.path = path;
      configs.put(path, config);
      long elapsed = System.currentTimeMillis() - startTime;
      logger.info("Успешно загружен конфиг " + path + ".yml (заняло " + elapsed + "ms)");
    }
    
    if (config != null) config.setManager(this);
    
    return config;
  }
  
  Path getPath(String path) {
    return Paths.get(plugin.getDataFolder().toString() + File.separatorChar + path + ".yml");
  }
  
  public void store(String path, YmlConfig object) {
    StringWriter writer = new StringWriter();
    
    try {
      mapper.writeValue(writer, object);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    Utils.writeString(getPath(path), writer.toString());
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
  
  public void reloadByCommand(CommandSender messagesOut) {
    for (Config config : configs.values()) {
      if (config instanceof Reloadable) {
        
        messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml");
        
        try {
          ((Reloadable) config).reload();
        } catch (Exception e) {
          messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml не удалась: " + e.getMessage());
        }
        messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml прошла успешно");
      }
    }
  }
  
  public void flush(String name, CommandSender messagesOut) {
    Utils.writeString(getPath(name), "");
    reloadByCommand(name, messagesOut);
    messagesOut.sendPlainMessage("Файл " + name + ".yml очищен");
  }
  
  public void reloadByCommand(String name, CommandSender messagesOut) {
    Optional<Config> optConfig = getByName(name);
    if (optConfig.isEmpty()) {
      messagesOut.sendPlainMessage("Конфик с именем " + name + " не найден или ещё не был загружен");
      return;
    }
    
    Config config = optConfig.get();
    
    if (config instanceof Reloadable) {
      
      messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml");
      
      try {
        ((Reloadable) config).reload();
      } catch (Exception e) {
        messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml не удалась: " + e.getMessage());
      }
      messagesOut.sendPlainMessage("Перезагрузка конфига " + config.path + ".yml прошла успешно");
    } else {
      messagesOut.sendPlainMessage("Конфиг " + name + " не может быть перезагружен");
    }
  }
  
  public Optional<Config> getByName(String name) {
    return Optional.ofNullable(configs.getOrDefault(name, null));
  }
  
  public void scheduleAutosave(int ticks, boolean async) {
    Scheduler<YmlConfigManager> scheduler = Scheduler.create(this).perform(x -> {
      logger.info("Автоматическое сохранение конфигов..");
      x.storeAll(true);
      logger.info("Всё сохранено");
    });
    
    if (async) scheduler.async();
    
    scheduler.register(plugin, ticks);
  }
}
