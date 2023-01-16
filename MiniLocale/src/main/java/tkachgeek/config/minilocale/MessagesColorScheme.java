package tkachgeek.config.minilocale;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class MessagesColorScheme {
  TextColor primary = NamedTextColor.GOLD;
  TextColor accent = NamedTextColor.YELLOW;
  
  public MessagesColorScheme(TextColor primary, TextColor accent) {
    this.primary = primary;
    this.accent = accent;
  }
  
  public MessagesColorScheme() {
  }
  
  public Message create(String message) {
    String accentStart = "<#" + accent.asHexString() + ">";
    String accentEnd = "</#" + accent.asHexString() + ">";
    
    StringBuilder base = new StringBuilder("<#" + primary.asHexString() + ">");
    boolean started = false;
    
    for (var part : message.split("[<>]")) {
      if (!started) {
        started = true;
        base.append(accentStart).append("<").append(part);
      } else {
        started = false;
        base.append(part).append(">").append(accentEnd);
      }
    }
    return new Message(base.toString());
  }
}
