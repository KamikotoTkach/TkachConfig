package ru.cwcode.tkach.config.webeditor.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SchemaServiceTest {
  private final SchemaService schemaService = new SchemaService();

  @Test
  public void generatesDraft7SchemaForScalarsEnumsContainersAndJacksonProperties() throws Exception {
    JsonNode schema = schemaService.generateConfiguredSchema(jsonMapper(), Fixture.class);
    JsonNode properties = schema.path("properties");

    assertEquals("http://json-schema.org/draft-07/schema#", schema.path("$schema").textValue());
    assertEquals("string", properties.path("name").path("type").textValue());
    assertEquals("integer", properties.path("count").path("type").textValue());
    assertTrue(containsText(properties.path("mode"), "enum", "FIRST"));
    assertEquals("array", properties.path("names").path("type").textValue());
    assertEquals("array", properties.path("aliases").path("type").textValue());
    assertEquals("array", properties.path("tags").path("type").textValue());
    assertTrue(properties.path("tags").path("uniqueItems").booleanValue());
    assertEquals("date", properties.path("date").path("format").textValue());
    assertEquals("date-time", properties.path("timestamp").path("format").textValue());
    assertTrue(properties.has("renamed"));
    assertFalse(properties.has("originalName"));
    assertFalse(properties.has("ignored"));
    assertTrue(properties.path("naming").path("properties").has("camel_case"));
    assertFalse(properties.path("naming").path("properties").has("camelCase"));
  }

  @Test
  public void generatesDeclaredPolymorphicAlternativesAndRequiredDiscriminator() throws Exception {
    JsonNode schema = schemaService.generateConfiguredSchema(jsonMapper(), Fixture.class);
    JsonNode polymorphicSchema = schema.path("properties").path("polymorphic");

    assertTrue(containsKey(polymorphicSchema, "oneOf"));
    assertTrue(containsText(polymorphicSchema, "const", "alpha"));
    assertTrue(containsText(polymorphicSchema, "const", "beta"));
    assertTrue(containsKeyValue(polymorphicSchema, "alphaValue", "type", "string"));
    assertTrue(containsKeyValue(polymorphicSchema, "betaValue", "type", "integer"));
    assertTrue(containsKeyValue(polymorphicSchema, "common", "type", "string"));
    assertTrue(hasRequiredProperty(polymorphicSchema, "kind"));
  }

  @Test
  public void supportsProjectJsonAndYamlMapperKindsAndRepeatedGeneration() throws Exception {
    ObjectMapper jsonMapper = jsonMapper();
    ObjectMapper yamlMapper = yamlMapper();

    JsonNode firstJsonSchema = schemaService.generateDefaultSchema(jsonMapper, Fixture.class);
    JsonNode secondJsonSchema = schemaService.generateDefaultSchema(jsonMapper, Fixture.class);
    JsonNode yamlSchema = schemaService.generateConfiguredSchema(yamlMapper, Fixture.class);
    Fixture jsonFixture = new Fixture();
    jsonFixture.polymorphic = new Alpha();
    Fixture yamlFixture = new Fixture();
    yamlFixture.polymorphic = new Beta();

    assertNotNull(firstJsonSchema);
    assertEquals(firstJsonSchema, secondJsonSchema);
    assertEquals("http://json-schema.org/draft-07/schema#", yamlSchema.path("$schema").textValue());
    assertTrue(containsText(yamlSchema.path("properties").path("polymorphic"), "const", "alpha"));
    assertTrue(jsonMapper.readValue(jsonMapper.writeValueAsString(jsonFixture), Fixture.class).polymorphic instanceof Alpha);
    assertTrue(yamlMapper.readValue(yamlMapper.writeValueAsString(yamlFixture), Fixture.class).polymorphic instanceof Beta);
  }

  @Test
  public void keepsConfiguredEditorMetadataSeparateFromDefaultSchema() throws Exception {
    JsonNode configured = schemaService.generateConfiguredSchema(jsonMapper(), Fixture.class);
    JsonNode defaults = schemaService.generateDefaultSchema(jsonMapper(), Fixture.class);

    assertEquals("names", configured.path("properties").path("names").path("title").textValue());
    assertEquals("table", configured.path("properties").path("names").path("format").textValue());
    assertFalse(defaults.path("properties").path("names").has("format"));
  }

  private ObjectMapper jsonMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }

  private ObjectMapper yamlMapper() {
    return new ObjectMapper(new YAMLFactory()).registerModule(new JavaTimeModule());
  }

  private boolean containsKey(JsonNode node, String key) {
    if (node.isObject() && node.has(key)) {
      return true;
    }
    for (JsonNode child : node) {
      if (containsKey(child, key)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsText(JsonNode node, String key, String value) {
    if (node.isObject() && value.equals(node.path(key).textValue())) {
      return true;
    }
    if (node.isObject() && node.path(key).isArray()) {
      for (JsonNode item : node.path(key)) {
        if (value.equals(item.textValue())) {
          return true;
        }
      }
    }
    for (JsonNode child : node) {
      if (containsText(child, key, value)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsKeyValue(JsonNode node, String property, String key, String value) {
    if (node.isObject() && value.equals(node.path(property).path(key).textValue())) {
      return true;
    }
    for (JsonNode child : node) {
      if (containsKeyValue(child, property, key, value)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasRequiredProperty(JsonNode node, String property) {
    JsonNode required = node.path("required");
    if (required.isArray()) {
      for (JsonNode item : required) {
        if (property.equals(item.textValue())) {
          return true;
        }
      }
    }
    for (JsonNode child : node) {
      if (hasRequiredProperty(child, property)) {
        return true;
      }
    }
    return false;
  }

  public enum Mode {
    FIRST,
    SECOND
  }

  public static class Fixture {
    public String name;
    public int count;
    public Mode mode;
    public List<String> names;
    public String[] aliases;
    public Set<Integer> tags;
    public LocalDate date;
    public LocalDateTime timestamp;
    @JsonProperty("renamed")
    public String originalName;
    @JsonIgnore
    public String ignored;
    public NamingFixture naming;
    public Polymorphic polymorphic;
  }

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class NamingFixture {
    public String camelCase;
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = Alpha.class, name = "alpha"),
    @JsonSubTypes.Type(value = Beta.class)
  })
  public abstract static class Polymorphic {
    public String common;
  }

  public static class Alpha extends Polymorphic {
    public String alphaValue;
  }

  @JsonTypeName("beta")
  public static class Beta extends Polymorphic {
    public int betaValue;
  }
}
