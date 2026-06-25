package ru.cwcode.tkach.config.webeditor.service.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;
import ru.cwcode.tkach.config.webeditor.service.MapperFactory;
import ru.cwcode.tkach.config.webeditor.service.SchemaService;

public class JsonEditorFormatAdapter implements ConfigFormatAdapter {
  private final MapperFactory mapperFactory;
  private final SchemaService schemaService;
  
  public JsonEditorFormatAdapter(MapperFactory mapperFactory, SchemaService schemaService) {
    this.mapperFactory = mapperFactory;
    this.schemaService = schemaService;
  }
  
  @Override
  public EditorContent read(YmlConfigManager manager, YmlConfig config) throws Exception {
    ObjectMapper mapper = mapperFactory.createJsonMapper(manager);
    JsonNode schema = schemaService.generateDefaultSchema(mapper, config.getClass());
    return new EditorContent(mapper.writeValueAsString(config), schema.toPrettyString());
  }
  
  @Override
  public YmlConfig parse(YmlConfigManager manager, YmlConfig currentConfig, String content) throws Exception {
    return mapperFactory.createJsonMapper(manager).readValue(content, currentConfig.getClass());
  }
}
