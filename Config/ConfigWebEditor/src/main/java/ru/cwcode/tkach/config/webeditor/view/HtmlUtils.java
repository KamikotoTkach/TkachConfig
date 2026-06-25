package ru.cwcode.tkach.config.webeditor.view;

public final class HtmlUtils {
  private HtmlUtils() {
  }
  
  public static String escape(String value) {
    return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
  }
  
  public static String escapeAttribute(String value) {
    return escape(value);
  }
}
