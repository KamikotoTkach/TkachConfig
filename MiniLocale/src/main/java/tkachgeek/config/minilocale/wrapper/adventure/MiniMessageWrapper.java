package tkachgeek.config.minilocale.wrapper.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import tkachgeek.config.minilocale.Placeholders;

import java.util.ArrayList;
import java.util.List;

public class MiniMessageWrapper {
  
  private static final MiniMessage mm = MiniMessage.get();
  
  //todo: implement legacy parser to minimessage
  public static String serialize(Component component) {
    return mm.serialize(component);
  }
  
  public static List<String> serialize(Component... component) {
    List<String> strings = new ArrayList<>();
    
    for (Component c : component) {
      strings.add(mm.serialize(c));
    }
    
    return strings;
  }
  
  public static List<String> serialize(List<Component> components) {
    List<String> strings = new ArrayList<>();
    
    for (Component c : components) {
      strings.add(mm.serialize(c));
    }
    
    return strings;
  }
  
  public static List<Component> deserialize(List<String> strings) {
    if (strings == null) return null;
    
    List<Component> components = new ArrayList<>();
    
    for (String s : strings) {
      components.add(mm.deserialize(replaceSection(s)));
    }
    
    return components;
  }
  
  public static List<Component> deserialize(String... string) {
    List<Component> components = new ArrayList<>();
    
    for (String s : string) {
      components.add(mm.deserialize(replaceSection(s)));
    }
    
    return components;
  }
  
  public static Component deserialize(String string) {
    if (string == null) return null;
    
    return mm.deserialize(replaceSection(string));
  }
  
  public static Component deserialize(String string, boolean disableItalic) {
    if (string == null) return null;
    
    Component deserialized = mm.deserialize(replaceSection(string));
    
    if (disableItalic) {
      return deserialized.decoration(TextDecoration.ITALIC, false);
    }
    
    return deserialized;
  }
  
  @NotNull
  private static String replaceSection(String string) {
    return string.replace('§', '&');
  }
  
  public static Component deserialize(String string, Placeholders placeholders) {
    if (string == null) return null;
    
    return mm.parse(replaceSection(string), placeholders.getResolvers());
  }
  
  public static Component deserialize(String string, Placeholders placeholders, boolean disableItalic) {
    if (string == null) return null;
    
    Component deserialized = mm.parse(replaceSection(string), placeholders.getResolvers());
    
    if (disableItalic) {
      return deserialized.decoration(TextDecoration.ITALIC, false);
    }
    
    return deserialized;
  }
  
  public static List<Component> deserialize(List<String> strings, Placeholders placeholders, boolean disableItalic) {
    if (strings == null) return null;
    
    List<Component> components = new ArrayList<>();
    
    TextDecoration italicDecoration = TextDecoration.ITALIC;
    
    for (String s : strings) {
      Component deserialized = mm.parse(replaceSection(s), placeholders.getResolvers());
      
      if (disableItalic) {
        deserialized = deserialized.decoration(italicDecoration, false);
      }
      
      components.add(deserialized);
    }
    
    return components;
  }
}
