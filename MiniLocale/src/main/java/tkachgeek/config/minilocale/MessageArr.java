package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import tkachgeek.config.minilocale.wrapper.MiniMessageWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageArr {
  String[] message;
  
  public MessageArr(String... message) {
    this.message = message;
  }
  
  public MessageArr() {
  }
  
  public void send(Audience audience) {
    for (String line : message) {
      audience.sendMessage(MiniMessageWrapper.deserialize(line));
    }
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    for (String line : message) {
      audience.sendMessage(MiniMessageWrapper.deserialize(line, placeholders));
    }
  }
  
  public Collection<Component> get() {
    List<Component> list = new ArrayList<>();
    for (String line : message) {
      list.add(MiniMessageWrapper.deserialize(line));
    }
    return list;
  }
  
  public Collection<Component> get(Placeholders placeholders) {
    List<Component> list = new ArrayList<>();
    for (String line : message) {
      list.add(MiniMessageWrapper.deserialize(line, placeholders));
    }
    return list;
  }
}
