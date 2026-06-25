package ru.cwcode.tkach.config.webeditor.service;

import ru.cwcode.tkach.config.base.Config;
import ru.cwcode.tkach.config.base.manager.ConfigManager;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;
import ru.cwcode.tkach.config.webeditor.service.format.ConfigFormatAdapter;
import ru.cwcode.tkach.config.webeditor.service.format.EditorContent;
import ru.cwcode.tkach.config.webeditor.service.format.JsonEditorFormatAdapter;
import ru.cwcode.tkach.config.webeditor.service.format.NativeYamlFormatAdapter;
import ru.cwcode.tkach.config.webeditor.service.format.SchemaYamlFormatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class ConfigEditorService {
  private final List<BiConsumer<YmlConfig, YmlConfig>> reloadListeners;
  private final MapperFactory mapperFactory = new MapperFactory();
  private final SchemaService schemaService = new SchemaService();
  private final Map<EditorFormat, ConfigFormatAdapter> adapters = new HashMap<>();
  
  public ConfigEditorService(List<BiConsumer<YmlConfig, YmlConfig>> reloadListeners) {
    this.reloadListeners = reloadListeners;
    adapters.put(EditorFormat.VISUAL_JSON, new JsonEditorFormatAdapter(mapperFactory, schemaService));
    adapters.put(EditorFormat.SCHEMA_YAML, new SchemaYamlFormatAdapter(mapperFactory, schemaService));
    adapters.put(EditorFormat.NATIVE_YAML, new NativeYamlFormatAdapter());
  }
  
  public Set<String> getNamespaces() {
    return ConfigManager.managers.keySet();
  }
  
  public ConfigListData getConfigList(String namespace) {
    ConfigManager<? extends Config<?>> manager = ConfigManager.managers.get(namespace);
    if (manager == null) {
      return null;
    }
    return new ConfigListData(namespace, new ArrayList<>(manager.getConfigNames(c -> true)));
  }
  
  public ConfigData getConfigData(String namespace, String name, EditorFormat format) throws Exception {
    YmlConfigContext context = findYmlConfig(namespace, name);
    if (context == null) {
      return null;
    }
    
    EditorContent content = adapters.get(format).read(context.manager(), context.config());
    
    return new ConfigData(namespace, name, content.content(), content.schema());
  }
  
  public UpdateResult update(String namespace, String name, String body, EditorFormat format) {
    try {
      YmlConfigContext context = findYmlConfig(namespace, name);
      if (context == null) {
        return UpdateResult.error(404, "Config not found");
      }
      
      YmlConfig newConfig = adapters.get(format).parse(context.manager(), context.config(), body);
      
      context.manager().updateConfig(name, newConfig);
      for (BiConsumer<YmlConfig, YmlConfig> listener : reloadListeners) {
        listener.accept(context.config(), newConfig);
      }
      newConfig.save();
      
      return UpdateResult.ok();
    } catch (Exception e) {
      return UpdateResult.error(500, e.getMessage() == null ? "Config update failed" : e.getMessage());
    }
  }
  
  private YmlConfigContext findYmlConfig(String namespace, String name) {
    ConfigManager<? extends Config<?>> manager = ConfigManager.managers.get(namespace);
    if (!(manager instanceof YmlConfigManager ymlManager)) {
      return null;
    }
    
    return ymlManager.findConfig(name)
                     .map(config -> new YmlConfigContext(ymlManager, config))
                     .orElse(null);
  }
  
  private record YmlConfigContext(YmlConfigManager manager, YmlConfig config) {
  }
  
  public record ConfigListData(String namespace, List<String> configNames) {
  }
  
  public record ConfigData(String namespace, String name, String content, String schema) {
  }
  
  public enum EditorFormat {
    VISUAL_JSON,
    SCHEMA_YAML,
    NATIVE_YAML
  }
}
