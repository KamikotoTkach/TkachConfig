package ru.cwcode.tkach.config.webeditor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;

import java.util.HashSet;
import java.util.Set;

public class SchemaService {
  public JsonNode generateConfiguredSchema(ObjectMapper mapper, Class<?> configClass) throws JsonMappingException {
    SchemaGeneratorConfigBuilder builder = createConfigBuilder(mapper);
    builder.forFields().withTitleResolver(field -> field.isFakeContainerItemScope() ? null : field.getSchemaPropertyName());
    builder.forMethods().withTitleResolver(method -> method.isFakeContainerItemScope() ? null : method.getSchemaPropertyName());
    builder.forTypesInGeneral().withTypeAttributeOverride((schema, scope, context) -> {
      if (scope.isContainerType()) {
        schema.put("format", "table");
      }
    });
    return generateSchema(builder, configClass);
  }

  public JsonNode generateDefaultSchema(ObjectMapper mapper, Class<?> configClass) throws JsonMappingException {
    return generateSchema(createConfigBuilder(mapper), configClass);
  }

  private SchemaGeneratorConfigBuilder createConfigBuilder(ObjectMapper mapper) {
    SchemaGeneratorConfigBuilder builder = new SchemaGeneratorConfigBuilder(mapper, SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON)
      .with(new JacksonModule(
        JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE,
        JacksonOption.FLATTENED_ENUMS_FROM_JSONPROPERTY,
        JacksonOption.RESPECT_JSONPROPERTY_ORDER,
        JacksonOption.RESPECT_JSONPROPERTY_REQUIRED
      ));
    builder.forTypesInGeneral().withArrayUniqueItemsResolver(scope ->
      Set.class.isAssignableFrom(scope.getType().getErasedType()) ? true : null
    );
    return builder;
  }

  private JsonNode generateSchema(SchemaGeneratorConfigBuilder builder, Class<?> configClass) {
    JsonNode schema = new SchemaGenerator(builder.build()).generateSchema(configClass);
    useOneOfForJacksonSubtypes(schema);
    return schema;
  }

  private void useOneOfForJacksonSubtypes(JsonNode node) {
    for (JsonNode child : node) {
      useOneOfForJacksonSubtypes(child);
    }
    if (!(node instanceof ObjectNode objectNode)) {
      return;
    }
    JsonNode alternatives = objectNode.get("anyOf");
    if (alternatives instanceof ArrayNode alternativesArray && hasDistinctRequiredDiscriminatorValues(alternativesArray)) {
      objectNode.set("oneOf", alternatives);
      objectNode.remove("anyOf");
    }
  }

  private boolean hasDistinctRequiredDiscriminatorValues(ArrayNode alternatives) {
    if (alternatives.size() < 2) {
      return false;
    }
    JsonNode firstAlternative = alternatives.get(0);
    for (JsonNode requiredProperty : firstAlternative.path("required")) {
      String propertyName = requiredProperty.textValue();
      JsonNode firstValue = firstAlternative.path("properties").path(propertyName).path("const");
      if (!firstValue.isValueNode()) {
        continue;
      }
      Set<JsonNode> values = new HashSet<>();
      values.add(firstValue);
      for (int index = 1; index < alternatives.size(); index++) {
        JsonNode alternative = alternatives.get(index);
        JsonNode value = alternative.path("properties").path(propertyName).path("const");
        if (!value.isValueNode() || !containsText(alternative.path("required"), propertyName)) {
          break;
        }
        values.add(value);
      }
      if (values.size() == alternatives.size()) {
        return true;
      }
    }
    return false;
  }

  private boolean containsText(JsonNode array, String value) {
    for (JsonNode item : array) {
      if (value.equals(item.textValue())) {
        return true;
      }
    }
    return false;
  }
}
