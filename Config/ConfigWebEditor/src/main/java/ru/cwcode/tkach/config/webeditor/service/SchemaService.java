package ru.cwcode.tkach.config.webeditor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import java.util.HashSet;
import java.util.Set;

public class SchemaService {
  private static final JsonSchemaConfig JSON_SCHEMA_CONFIG = JsonSchemaConfig.builder()
                                                                            .jsonSchemaDraft(JsonSchemaDraft.DRAFT_07)
                                                                            .autoGenerateTitleForProperties(true)
                                                                            .defaultArrayFormat("table")
                                                                            .useOneOfForOption(true)
                                                                            .usePropertyOrdering(true)
                                                                            .hidePolymorphismTypeProperty(true)
                                                                            .useMinLengthForNotNull(true)
                                                                            .customType2FormatMapping(JsonSchemaConfig.DEFAULT_DATE_FORMAT_MAPPING)
                                                                            .useMultipleEditorSelectViaProperty(true)
                                                                            .uniqueItemClasses(new HashSet<>() {
                                                                              {
                                                                                this.add(Set.class);
                                                                              }
                                                                            }).build();
  
  public JsonNode generateConfiguredSchema(ObjectMapper mapper, Class<?> configClass) throws com.fasterxml.jackson.databind.JsonMappingException {
    return new JsonSchemaGenerator(mapper, JSON_SCHEMA_CONFIG).generateJsonSchema(configClass);
  }
  
  public JsonNode generateDefaultSchema(ObjectMapper mapper, Class<?> configClass) throws com.fasterxml.jackson.databind.JsonMappingException {
    return new JsonSchemaGenerator(mapper).generateJsonSchema(configClass);
  }
}
