package ru.cwcode.tkach.config.webeditor.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public record WebRequest(String method, String path, String body, String basePath) {
  private static final int MAX_BODY_SIZE = 2 * 1024 * 1024;
  
  public static WebRequest from(HttpExchange exchange) throws IOException {
    return new WebRequest(exchange.getRequestMethod(),
                          exchange.getRequestURI().getPath(),
                          readBody(exchange),
                          getBasePath(exchange));
  }
  
  private static String readBody(HttpExchange exchange) throws IOException {
    try (InputStream inputStream = exchange.getRequestBody();
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[8192];
      int total = 0;
      int read;
      while ((read = inputStream.read(buffer)) != -1) {
        total += read;
        if (total > MAX_BODY_SIZE) {
          throw new BodyTooLargeException("Request body is too large");
        }
        outputStream.write(buffer, 0, read);
      }
      return outputStream.toString(StandardCharsets.UTF_8);
    }
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
  
  public static class BodyTooLargeException extends IOException {
    public BodyTooLargeException(String message) {
      super(message);
    }
  }
}
