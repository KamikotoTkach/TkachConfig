package ru.cwcode.tkach.config.webeditor.service.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;
import ru.cwcode.tkach.config.webeditor.service.MapperFactory;
import ru.cwcode.tkach.config.webeditor.service.SchemaService;

public class SchemaYamlFormatAdapter implements ConfigFormatAdapter {
  private final MapperFactory mapperFactory;
  private final SchemaService schemaService;
  
  public SchemaYamlFormatAdapter(MapperFactory mapperFactory, SchemaService schemaService) {
    this.mapperFactory = mapperFactory;
    this.schemaService = schemaService;
  }
  
  @Override
  public EditorContent read(YmlConfigManager manager, YmlConfig config) throws Exception {
    ObjectMapper mapper = mapperFactory.createYamlMapper(manager);
    JsonNode schema = schemaService.generateConfiguredSchema(mapper, config.getClass());
    return new EditorContent(mapper.writeValueAsString(config), schema.toPrettyString());
  }
  
  @Override
  public YmlConfig parse(YmlConfigManager manager, YmlConfig currentConfig, String content) throws Exception {
    return mapperFactory.createYamlMapper(manager).readValue(content, currentConfig.getClass());
  }
}
