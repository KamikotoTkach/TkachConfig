package ru.cwcode.tkach.config.webeditor.view;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class UrlUtils {
  private UrlUtils() {
  }
  
  public static String path(String value) {
    return Arrays.stream(value.split("/", -1))
                 .map(UrlUtils::segment)
                 .collect(Collectors.joining("/"));
  }
  
  private static String segment(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
