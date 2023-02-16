package tkachgeek.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Logger;
@Deprecated
public abstract class JsonConfigManager {
  static public HashMap<String, Config> configs = new HashMap<>();
  static JavaPlugin plugin;
  static GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
  static Gson gson = builder.create();
  
  public static void init(JavaPlugin plugin) {
    JsonConfigManager.plugin = plugin;
  }
  
  public static <T> void registerTypeAdapter(Class<T> type, TypeAdapter<T> typeAdapter) {
    builder.registerTypeAdapter(type, typeAdapter);
    gson = builder.create();
  }
  
  public static <T extends JsonConfig> T load(String path, Class<T> type) {
    String json = Utils.readString(getPath(path));
    T config;
    if (json.length() == 0) {
      config = Utils.getNewInstance(type);
    } else {
      config = gson.fromJson(json, type);
    }
    if (config != null) {
      config.path = path;
      configs.put(path, config);
    } else {
      Logger.getGlobal().warning("Cannot make instance of " + type.getSimpleName());
    }
    return config;
  }
  
  public static void store(String path, Object object) {
    String json = gson.toJson(object);
    Utils.writeString(getPath(path), json);
  }
  
  static Path getPath(String path) {
    return Paths.get(plugin.getDataFolder().toString() + File.separatorChar + path + ".json");
  }
  
  public static void storeAll() {
    configs.values().stream().filter(config -> config.storeAllEnabled).forEach(Config::store);
  }
  
  public static String toString(JsonConfig config) {
    return gson.toJson(config);
  }
}
