package ru.cwcode.tkach.config.webeditor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.yaml.snakeyaml.LoaderOptions;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfigManager;

public class MapperFactory {
  public ObjectMapper createJsonMapper(YmlConfigManager manager) {
    ObjectMapper objectMapper = new ObjectMapper();
    manager.mapper().configureObjectMapper(objectMapper);
    return objectMapper;
  }
  
  public ObjectMapper createYamlMapper(YmlConfigManager manager) {
    ObjectMapper objectMapper = new ObjectMapper(createYamlFactory());
    manager.mapper().configureObjectMapper(objectMapper);
    return objectMapper;
  }
  
  private YAMLFactory createYamlFactory() {
    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setCodePointLimit(100 * 1024 * 1024);
    
    return YAMLFactory.builder()
                      .disable(YAMLGenerator.Feature.SPLIT_LINES)
                      .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
                      .disable(YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID)
                      .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                      .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
                      .loaderOptions(loaderOptions)
                      .build();
  }
}
