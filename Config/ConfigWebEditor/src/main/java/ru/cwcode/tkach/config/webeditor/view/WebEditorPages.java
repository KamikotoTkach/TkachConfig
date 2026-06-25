package ru.cwcode.tkach.config.webeditor.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class WebEditorPages {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TemplateRenderer renderer = new TemplateRenderer("/webeditor/templates");
  
  public String indexPage(String basePath, Set<String> namespaces) {
    StringBuilder elements = new StringBuilder();
    for (String namespace : namespaces) {
      elements.append(renderer.render("namespace-item.html", Map.of(
        "href", HtmlUtils.escapeAttribute(basePath + "/index/" + UrlUtils.path(namespace)),
        "name", HtmlUtils.escape(namespace)
      )));
    }
    
    return renderer.render("index.html", Map.of("namespaces", elements.toString()));
  }
  
  public String configListPage(String basePath, String namespace, List<String> configs) {
    StringBuilder elements = new StringBuilder();
    for (String config : configs) {
      elements.append(renderer.render("config-item.html", Map.of(
        "yamlEditorUrl", HtmlUtils.escapeAttribute(basePath + "/edit-yaml/" + UrlUtils.path(namespace) + "/" + UrlUtils.path(config)),
        "rawEditorUrl", HtmlUtils.escapeAttribute(basePath + "/edit-raw/" + UrlUtils.path(namespace) + "/" + UrlUtils.path(config)),
        "visualEditorUrl", HtmlUtils.escapeAttribute(basePath + "/edit/" + UrlUtils.path(namespace) + "/" + UrlUtils.path(config)),
        "name", HtmlUtils.escape(config)
      )));
    }
    
    return renderer.render("config-list.html", Map.of("configs", elements.toString()));
  }
  
  public String yamlEditorPage(String basePath, String namespace, String name, String currentYaml, String schema, boolean nativeRaw) {
    return renderer.render("raw-editor.html", Map.ofEntries(
      entry("title", HtmlUtils.escape((nativeRaw ? "Raw YAML - " : "YAML Editor - ") + name)),
      entry("editorTitle", nativeRaw ? "Raw YAML" : "YAML"),
      entry("namespace", toJson(namespace)),
      entry("namespacePath", toJson(UrlUtils.path(namespace))),
      entry("namespaceLabel", HtmlUtils.escape(namespace)),
      entry("name", toJson(name)),
      entry("namePath", toJson(UrlUtils.path(name))),
      entry("nameLabel", HtmlUtils.escape(name)),
      entry("basePath", HtmlUtils.escapeAttribute(basePath)),
      entry("basePathJson", toJson(basePath)),
      entry("currentYaml", toJson(currentYaml)),
      entry("schema", schema),
      entry("schemaEnabled", nativeRaw ? "false" : "true"),
      entry("updatePath", toJson(nativeRaw ? "/update-raw/" : "/update-yaml/"))
    ));
  }
  
  public String visualEditorPage(String basePath, String namespace, String name, String currentJson, String schema) {
    return renderer.render("visual-editor.html", Map.of(
      "basePathJson", toJson(basePath),
      "starting", currentJson,
      "schema", schema,
      "namespace", toJson(namespace),
      "namespacePath", toJson(UrlUtils.path(namespace)),
      "name", toJson(name),
      "namePath", toJson(UrlUtils.path(name))
    ));
  }
  
  private String toJson(String value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot write json string", e);
    }
  }
}
