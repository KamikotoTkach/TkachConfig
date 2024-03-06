package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Placeholders {
  private final HashMap<String, Template> resolvers = new HashMap<>();
  
  public Placeholders() {
  }
  
  public Placeholders(String key, String value) {
    add(key, value);
  }
  
  public Placeholders(Template resolver) {
    add(resolver);
  }
  
  public Placeholders add(String key, String value) {
    resolvers.put(key, Template.of(key, value));
    return this;
  }
  
  public Placeholders add(Template template) {
    resolvers.put(template.toString(), template);
    return this;
  }
  
  public Placeholders add(String key, Component value) {
    resolvers.put(key, Template.of(key, value));
    return this;
  }
  
  public List<Template> getResolvers() {
    return new ArrayList<>(resolvers.values());
  }
  
  public Placeholders add(String key, double value) {
    return add(key, String.valueOf(value));
  }
  
  public Placeholders add(String key, int value) {
    return add(key, String.valueOf(value));
  }
  
  public Placeholders add(String key, float value) {
    return add(key, String.valueOf(value));
  }
  
  public Placeholders add(String key, long value) {
    return add(key, String.valueOf(value));
  }
  
  public Placeholders add(String key, boolean value) {
    return add(key, String.valueOf(value));
  }
}
