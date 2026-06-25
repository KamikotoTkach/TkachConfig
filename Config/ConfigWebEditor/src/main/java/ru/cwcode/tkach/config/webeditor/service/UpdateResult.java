package ru.cwcode.tkach.config.webeditor.service;

public record UpdateResult(boolean success, int status, String message) {
  public static UpdateResult ok() {
    return new UpdateResult(true, 200, "");
  }
  
  public static UpdateResult error(int status, String message) {
    return new UpdateResult(false, status, message);
  }
}
