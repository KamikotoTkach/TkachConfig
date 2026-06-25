package ru.cwcode.tkach.config.webeditor.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record WebRequest(String method, String path, String body, String basePath) {
  public static WebRequest from(HttpExchange exchange) throws IOException {
    return new WebRequest(exchange.getRequestMethod(),
                          exchange.getRequestURI().getPath(),
                          new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                          getBasePath(exchange));
  }
  
  private static String getBasePath(HttpExchange exchange) {
    String prefix = exchange.getRequestHeaders().getFirst("X-Forwarded-Prefix");
    if (prefix != null && !prefix.isBlank()) {
      if (prefix.endsWith("/")) {
        prefix = prefix.substring(0, prefix.length() - 1);
      }
      return prefix;
    }
    return "";
  }
}
