package ru.cwcode.tkach.config.webeditor.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record WebResponse(int status, String contentType, byte[] body) {
  public static WebResponse html(String body) {
    return text(200, "text/html; charset=utf-8", body);
  }
  
  public static WebResponse json(int status, String body) {
    return text(status, "application/json; charset=utf-8", body);
  }
  
  public static WebResponse text(int status, String body) {
    return text(status, "text/plain; charset=utf-8", body);
  }
  
  public static WebResponse bytes(int status, String contentType, byte[] body) {
    return new WebResponse(status, contentType, body);
  }
  
  private static WebResponse text(int status, String contentType, String body) {
    return new WebResponse(status, contentType, body.getBytes(StandardCharsets.UTF_8));
  }
  
  public void send(HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, body.length);
    try (var responseBody = exchange.getResponseBody()) {
      responseBody.write(body);
    }
  }
}
