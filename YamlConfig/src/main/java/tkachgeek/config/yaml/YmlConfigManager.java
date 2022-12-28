package tkachgeek.config.yaml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Reloadable;
import tkachgeek.config.base.Utils;
import tkachgeek.config.yaml.module.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YmlConfigManager {
  static public HashMap<String, Config> configs = new HashMap<>();
  static JavaPlugin plugin;
  static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  
  public static ObjectMapper getMapper() {
    return mapper;
  }
  
  public static void init(JavaPlugin plugin) {
    YmlConfigManager.plugin = plugin;
    
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
    
    YmlConfigManager.module(module);
  }
  
  public static void module(Module module) {
    mapper.registerModule(module);
  }
  
  public static <T extends YmlConfig> T load(String path, Class<T> type) {
    long startTime = System.currentTimeMillis();
    
    Logger.getLogger(plugin.getName()).log(Level.INFO, "");
    Logger.getLogger(plugin.getName()).log(Level.INFO, "Reading config " + path + ".yml");
    
    T config = null;
    String yaml = "";
    try {
      yaml = Utils.readString(getPath(path));
      
      if (yaml.length() == 0) {
        Logger.getLogger(plugin.getName()).log(Level.INFO, "File not found, default file will be used");
        config = Utils.getNewInstance(type);
      } else {
        config = mapper.readValue(yaml, type);
      }
    } catch (IOException e) {
      Logger.getLogger(plugin.getName()).log(Level.WARNING, "Unable to read the config " + path + ".yml");
      if (yaml.length() != 0) {
        String newPath = path + " " + new Timestamp(System.currentTimeMillis()).toString().replace(":", "-");
        Logger.getLogger(plugin.getName()).log(Level.WARNING, "The file is not empty, a copy was created under the name " + newPath + ".yml");
        Utils.writeString(getPath(newPath), yaml);
      }
      e.printStackTrace();
    }
    
    if (config == null) {
      Logger.getLogger(plugin.getName()).log(Level.WARNING, "Creating a config " + path + ".yml");
      config = Utils.getNewInstance(type);
    }
    
    if (config == null) {
      Logger.getLogger(plugin.getName()).log(Level.WARNING, "Failed to create a config " + path + ".yml (" + type.getSimpleName() + ")");
    } else {
      config.path = path;
      configs.put(path, config);
      long elapsed = System.currentTimeMillis() - startTime;
      Logger.getLogger(plugin.getName()).log(Level.INFO, "Successfully loaded the config " + path + ".yml (took " + elapsed + "ms)");
    }
    
    return config;
  }
  
  public static void store(String path, YmlConfig object) {
    StringWriter writer = new StringWriter();
    
    try {
      mapper.writeValue(writer, object);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    Utils.writeString(getPath(path), writer.toString());
  }
  
  static Path getPath(String path) {
    return Paths.get(plugin.getDataFolder().toString() + File.separatorChar + path + ".yml");
  }
  
  public static void storeAll() {
    for (Config config : configs.values()) {
      long start = System.currentTimeMillis();
      if (config.saveOnDisabling) {
        Logger.getLogger(plugin.getName()).log(Level.INFO, "");
        Logger.getLogger(plugin.getName()).log(Level.INFO, "Saving a config " + config.path + ".yml");
        
        try {
          config.store();
        } catch (Exception e) {
  
          Logger.getLogger(plugin.getName()).log(Level.WARNING, "Error when saving the config" + config.path + ".yml");
          
          e.printStackTrace();
          continue;
        }
        long elapsed = System.currentTimeMillis() - start;
        Logger.getLogger(plugin.getName()).log(Level.INFO, "Config " + config.path + ".yml saved (took " + elapsed + "ms)");
      }
    }
  }
  
  public static void reload() {
    for (Config config : configs.values()) {
      if (config instanceof Reloadable) {
  
        Logger.getLogger(plugin.getName()).log(Level.INFO, "Reloading the config " + config.path + ".yml");
        
        try {
          ((Reloadable) config).reload();
        } catch (Exception e) {
          Logger.getLogger(plugin.getName()).log(Level.WARNING, "Reloading the config " + config.path + ".yml failed: " + e.getMessage());
          continue;
        }
        Logger.getLogger(plugin.getName()).log(Level.INFO, "Reloading the config " + config.path + ".yml was successful");
      }
    }
  }
  
  public static String toString(YmlConfig config) {
    StringWriter writer = new StringWriter();
    
    try {
      mapper.writeValue(writer, config);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return writer.toString();
  }
}
