package ru.cwcode.tkach.config.webeditor.http;

import java.io.IOException;
import java.io.InputStream;

public class StaticFileHandler {
  private final String resourceRoot;
  
  public StaticFileHandler(String resourceRoot) {
    this.resourceRoot = resourceRoot;
  }
  
  public WebResponse handle(String path) {
    String resourcePath = resourceRoot + path;
    
    try (InputStream inputStream = StaticFileHandler.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        return WebResponse.text(404, "Not found");
      }
      return WebResponse.bytes(200, contentType(path), inputStream.readAllBytes());
    } catch (IOException e) {
      return WebResponse.text(500, "Cannot read static resource");
    }
  }
  
  private String contentType(String path) {
    if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
    if (path.endsWith(".css")) return "text/css; charset=utf-8";
    if (path.endsWith(".html")) return "text/html; charset=utf-8";
    if (path.endsWith(".json")) return "application/json; charset=utf-8";
    if (path.endsWith(".ttf")) return "font/ttf";
    return "application/octet-stream";
  }
}
