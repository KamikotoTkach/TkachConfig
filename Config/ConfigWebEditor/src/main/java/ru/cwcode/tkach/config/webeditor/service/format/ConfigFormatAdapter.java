package ru.cwcode.tkach.config.webeditor.service.format;

import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;

public interface ConfigFormatAdapter {
  EditorContent read(YmlConfigManager manager, YmlConfig config) throws Exception;
  
  YmlConfig parse(YmlConfigManager manager, YmlConfig currentConfig, String content) throws Exception;
}
