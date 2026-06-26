package ru.cwcode.tkach.config.webeditor.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService.ConfigData;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService.ConfigListData;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService.EditorFormat;
import ru.cwcode.tkach.config.webeditor.service.RawFileService;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawDirectoryData;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawFileData;
import ru.cwcode.tkach.config.webeditor.service.RawFileService.RawWriteResult;
import ru.cwcode.tkach.config.webeditor.service.UpdateResult;
import ru.cwcode.tkach.config.webeditor.view.WebEditorPages;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebEditorServer {
  private final int port;
  private final ConfigEditorService configService;
  private final RawFileService rawFileService;
  private final WebEditorPages pages = new WebEditorPages();
  private final StaticFileHandler staticFileHandler = new StaticFileHandler("/public");
  private HttpServer server;
  private ExecutorService executor;
  
  public WebEditorServer(int port, ConfigEditorService configService, RawFileService rawFileService) {
    this.port = port;
    this.configService = configService;
    this.rawFileService = rawFileService;
  }
  
  public void start() {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", this::handle);
      executor = Executors.newCachedThreadPool();
      server.setExecutor(executor);
      server.start();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot start web editor server on port " + port, e);
    }
  }
  
  public void stop() {
    if (server != null) {
      server.stop(0);
    }
    if (executor != null) {
      executor.shutdownNow();
    }
  }
  
  private void handle(HttpExchange exchange) throws IOException {
    WebResponse response;
    
    try {
      WebRequest request = WebRequest.from(exchange);
      response = route(request);
    } catch (WebRequest.BodyTooLargeException e) {
      response = WebResponse.text(413, e.getMessage());
    } catch (Exception e) {
      response = WebResponse.text(500, e.getMessage() == null ? "Internal server error" : e.getMessage());
    }
    
    response.send(exchange);
  }
  
  private WebResponse route(WebRequest request) throws Exception {
    String method = request.method();
    String path = request.path();
    
    if (method.equals("GET") && path.equals("/")) {
      return WebResponse.html(pages.indexPage(request.basePath(), configService.getNamespaces()));
    }
    
    if (method.equals("GET") && (path.startsWith("/monaco/") || path.startsWith("/editor/"))) {
      return staticFileHandler.handle(path);
    }
    
    if (method.equals("GET") && path.startsWith("/index/")) {
      String namespace = decode(path.substring("/index/".length()));
      ConfigListData data = configService.getConfigList(namespace);
      if (data == null) {
        return WebResponse.text(404, "Config manager not found");
      }
      return WebResponse.html(pages.configListPage(request.basePath(), data.namespace(), data.configNames()));
    }
    
    if (method.equals("GET") && (path.equals("/raw") || path.equals("/raw/"))) {
      return rawDirectoryResponse(request.basePath(), "");
    }
    
    if (method.equals("GET") && path.startsWith("/raw/browse/")) {
      return rawDirectoryResponse(request.basePath(), decode(path.substring("/raw/browse/".length())));
    }
    
    if (method.equals("GET") && path.startsWith("/raw/view/")) {
      return rawFileResponse(request.basePath(), decode(path.substring("/raw/view/".length())));
    }
    
    if (method.equals("POST") && path.startsWith("/raw/rename/")) {
      return rawWriteResponse(rawFileService.rename(decode(path.substring("/raw/rename/".length())), request.body()));
    }
    
    if (method.equals("POST") && path.startsWith("/raw/dir/")) {
      return rawWriteResponse(rawFileService.createDirectory(decode(path.substring("/raw/dir/".length()))));
    }
    
    if (method.equals("DELETE") && path.startsWith("/raw/dir/")) {
      return rawWriteResponse(rawFileService.deleteDirectory(decode(path.substring("/raw/dir/".length()))));
    }
    
    if (method.equals("PUT") && path.startsWith("/raw/file/")) {
      return rawWriteResponse(rawFileService.write(decode(path.substring("/raw/file/".length())), request.body()));
    }
    
    if (method.equals("POST") && path.startsWith("/raw/file/")) {
      return rawWriteResponse(rawFileService.create(decode(path.substring("/raw/file/".length())), request.body()));
    }
    
    if (method.equals("DELETE") && path.startsWith("/raw/file/")) {
      return rawWriteResponse(rawFileService.delete(decode(path.substring("/raw/file/".length()))));
    }
    
    if (method.equals("GET") && path.startsWith("/edit-yaml/")) {
      ConfigReference reference = parseConfigReference(path, "/edit-yaml/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      ConfigData data = configService.getConfigData(reference.namespace(), reference.name(), EditorFormat.SCHEMA_YAML);
      if (data == null) {
        return WebResponse.text(404, "Config not found");
      }
      return WebResponse.html(pages.yamlEditorPage(request.basePath(), data.namespace(), data.name(), data.content(), data.schema(), false));
    }
    
    if (method.equals("GET") && path.startsWith("/edit-raw/")) {
      ConfigReference reference = parseConfigReference(path, "/edit-raw/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      ConfigData data = configService.getConfigData(reference.namespace(), reference.name(), EditorFormat.NATIVE_YAML);
      if (data == null) {
        return WebResponse.text(404, "Config not found");
      }
      return WebResponse.html(pages.yamlEditorPage(request.basePath(), data.namespace(), data.name(), data.content(), data.schema(), true));
    }
    
    if (method.equals("GET") && path.startsWith("/edit/")) {
      ConfigReference reference = parseConfigReference(path, "/edit/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      ConfigData data = configService.getConfigData(reference.namespace(), reference.name(), EditorFormat.VISUAL_JSON);
      if (data == null) {
        return WebResponse.text(404, "Config not found");
      }
      return WebResponse.html(pages.visualEditorPage(request.basePath(), data.namespace(), data.name(), data.content(), data.schema()));
    }
    
    if (method.equals("PUT") && path.startsWith("/update-yaml/")) {
      ConfigReference reference = parseConfigReference(path, "/update-yaml/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      UpdateResult result = configService.update(reference.namespace(), reference.name(), request.body(), EditorFormat.SCHEMA_YAML);
      return updateResponse(result);
    }
    
    if (method.equals("PUT") && path.startsWith("/update-raw/")) {
      ConfigReference reference = parseConfigReference(path, "/update-raw/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      UpdateResult result = configService.update(reference.namespace(), reference.name(), request.body(), EditorFormat.NATIVE_YAML);
      return updateResponse(result);
    }
    
    if (method.equals("PUT") && path.startsWith("/update/")) {
      ConfigReference reference = parseConfigReference(path, "/update/");
      if (reference == null) {
        return WebResponse.text(404, "Config not found");
      }
      UpdateResult result = configService.update(reference.namespace(), reference.name(), request.body(), EditorFormat.VISUAL_JSON);
      return updateResponse(result);
    }
    
    return WebResponse.text(404, "Not found");
  }
  
  private WebResponse rawDirectoryResponse(String basePath, String path) throws IOException {
    try {
      RawDirectoryData data = rawFileService.list(path);
      return WebResponse.html(pages.rawDirectoryPage(basePath, data));
    } catch (SecurityException e) {
      return WebResponse.text(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return WebResponse.text(404, e.getMessage());
    }
  }
  
  private WebResponse rawFileResponse(String basePath, String path) throws IOException {
    try {
      RawFileData data = rawFileService.read(path);
      return WebResponse.html(pages.rawFileViewerPage(basePath, data));
    } catch (SecurityException e) {
      return WebResponse.text(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return WebResponse.text(404, e.getMessage());
    }
  }
  
  private WebResponse rawWriteResponse(RawWriteResult result) {
    if (result.success()) {
      return WebResponse.json(result.status(), "{\"success\":true}");
    }
    return WebResponse.text(result.status(), result.message());
  }
  
  private WebResponse updateResponse(UpdateResult result) {
    if (result.success()) {
      return WebResponse.json(200, "{\"success\":\"true\"}");
    }
    return WebResponse.text(result.status(), result.message());
  }
  
  private ConfigReference parseConfigReference(String path, String prefix) {
    String value = path.substring(prefix.length());
    int separator = value.indexOf('/');
    if (separator < 0 || separator == value.length() - 1) {
      return null;
    }
    
    return new ConfigReference(decode(value.substring(0, separator)), decode(value.substring(separator + 1)));
  }
  
  private String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }
  
  private record ConfigReference(String namespace, String name) {
  }
}
