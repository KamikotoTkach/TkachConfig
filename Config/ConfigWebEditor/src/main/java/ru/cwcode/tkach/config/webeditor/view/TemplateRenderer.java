package ru.cwcode.tkach.config.webeditor.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TemplateRenderer {
  private final String templateRoot;
  private final Map<String, String> cache = new HashMap<>();
  
  public TemplateRenderer(String templateRoot) {
    this.templateRoot = templateRoot;
  }
  
  public String render(String templateName, Map<String, String> placeholders) {
    String result = load(templateName);
    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
      result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return result;
  }
  
  private String load(String templateName) {
    return cache.computeIfAbsent(templateName, name -> {
      String path = templateRoot + "/" + name;
      try (InputStream inputStream = TemplateRenderer.class.getResourceAsStream(path)) {
        if (inputStream == null) {
          throw new IllegalArgumentException("Template not found: " + path);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot read template: " + path, e);
      }
    });
  }
}
