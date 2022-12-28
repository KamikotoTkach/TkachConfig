package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

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
      audience.sendMessage(MiniMessage.get().deserialize(line));
    }
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    for (String line : message) {
      audience.sendMessage(MiniMessage.get().parse(line, placeholders.getResolvers()));
    }
  }
  
  public Collection<Component> get() {
    List<Component> list = new ArrayList<>();
    for (String line : message) {
      list.add(MiniMessage.get().deserialize(line));
    }
    return list;
  }
  
  public Collection<Component> get(Placeholders placeholders) {
    List<Component> list = new ArrayList<>();
    for (String line : message) {
      list.add(MiniMessage.get().parse(line, placeholders.getResolvers()));
    }
    return list;
  }
}
