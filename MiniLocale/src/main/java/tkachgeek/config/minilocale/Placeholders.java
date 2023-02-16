package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import tkachgeek.tkachutils.text.nanoid.NanoID;

import java.util.HashMap;

public class Placeholders {
  private final HashMap<String, TagResolver> resolvers = new HashMap<>();
  
  public Placeholders(String key, String value) {
    add(key, value);
  }
  
  public Placeholders(TagResolver resolver) {
    add(resolver);
  }
  
  public Placeholders add(String key, String value) {
    resolvers.put(key, TagResolver.resolver(key.toLowerCase(), Tag.preProcessParsed(value)));
    return this;
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
  
  public Placeholders add(TagResolver tagResolver) {
    resolvers.put(NanoID.randomNanoId(), tagResolver);
    return this;
  }
  
  public Placeholders add(String key, Component value) {
    resolvers.put(key, TagResolver.resolver(key.toLowerCase(), Tag.inserting(value)));
    return this;
  }
  
  public TagResolver[] getResolvers() {
    return resolvers.values().toArray(new TagResolver[0]);
  }
}
