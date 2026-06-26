package ru.cwcode.tkach.config.webeditor.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawDirectoryData;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawFileData;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawFileEntry;

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
    
    return renderer.render("index.html", Map.of(
      "rawFilesUrl", HtmlUtils.escapeAttribute(basePath + "/raw"),
      "namespaces", elements.toString()
    ));
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
  
  public String rawDirectoryPage(String basePath, RawDirectoryData data) {
    StringBuilder elements = new StringBuilder();
    for (RawFileEntry entry : data.entries()) {
      elements.append(renderer.render("raw-file-entry.html", Map.of(
        "href", HtmlUtils.escapeAttribute(basePath + (entry.directory() ? "/raw/browse/" : "/raw/view/") + UrlUtils.path(entry.path())),
        "icon", entry.directory() ? "bi-folder-fill text-warning" : "bi-file-earmark-text text-secondary",
        "name", HtmlUtils.escape(entry.name()),
        "path", HtmlUtils.escapeAttribute(entry.path()),
        "pathUrl", HtmlUtils.escapeAttribute(UrlUtils.path(entry.path())),
        "type", entry.directory() ? "directory" : "file",
        "meta", HtmlUtils.escape(entry.directory() ? "Папка" : sizeLabel(entry.size()))
      )));
    }
    
    String parentButton = "";
    if (data.parentPath() != null) {
      String parentHref = data.parentPath().isBlank() ? basePath + "/raw" : basePath + "/raw/browse/" + UrlUtils.path(data.parentPath());
      parentButton = renderer.render("raw-parent-button.html", Map.of(
        "href", HtmlUtils.escapeAttribute(parentHref)
      ));
    }
    
    return renderer.render("raw-file-list.html", Map.of(
      "parentButton", parentButton,
      "path", HtmlUtils.escape(data.path().isBlank() ? "plugins" : "plugins/" + data.path()),
      "basePath", HtmlUtils.escapeAttribute(basePath),
      "basePathJson", toJson(basePath),
      "directoryPath", toJson(data.path()),
      "directoryPathUrl", toJson(UrlUtils.path(data.path())),
      "entries", elements.toString()
    ));
  }
  
  public String rawFileViewerPage(String basePath, RawFileData data) {
    return renderer.render("raw-file-viewer.html", Map.ofEntries(
      entry("title", HtmlUtils.escape("Raw file - " + data.name())),
      entry("basePath", HtmlUtils.escapeAttribute(basePath)),
      entry("basePathJson", toJson(basePath)),
      entry("pathLabel", HtmlUtils.escape("plugins/" + data.path())),
      entry("path", toJson(data.path())),
      entry("pathUrl", toJson(UrlUtils.path(data.path()))),
      entry("name", toJson(data.name())),
      entry("content", toJson(data.content())),
      entry("language", toJson(data.language()))
    ));
  }
  
  private String sizeLabel(long size) {
    if (size < 0) {
      return "Файл";
    }
    if (size < 1024) {
      return size + " B";
    }
    if (size < 1024 * 1024) {
      return String.format("%.1f KB", size / 1024.0);
    }
    return String.format("%.1f MB", size / 1024.0 / 1024.0);
  }
  
  private String toJson(String value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot write json string", e);
    }
  }
}
