package ru.cwcode.tkach.config.webeditor.service.format;

import ru.cwcode.tkach.config.Utils;
import ru.cwcode.tkach.config.base.ConfigPersistOptions;
import ru.cwcode.tkach.config.base.manager.ConfigMapper;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;

public class NativeYamlFormatAdapter implements ConfigFormatAdapter {
  @Override
  public EditorContent read(YmlConfigManager manager, YmlConfig config) {
    String content = Utils.readString(manager.getPath(config.name()));
    return new EditorContent(content, "null");
  }
  
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public YmlConfig parse(YmlConfigManager manager, YmlConfig currentConfig, String content) throws Exception {
    ConfigMapper.MappingResult result = manager.mapper().map(content, currentConfig.getClass(), ConfigPersistOptions.DEFAULT);
    if (result.getConfig().isEmpty()) {
      ConfigMapper.MappingException exception = (ConfigMapper.MappingException) result.getException().orElse(null);
      if (exception == null) {
        throw new IllegalArgumentException("Cannot parse native YAML");
      }
      throw new IllegalArgumentException("Line %s, column %s: %s".formatted(exception.line(), exception.column(), exception.message()));
    }
    return (YmlConfig) result.getConfig().orElseThrow();
  }
}
