package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;

public class MessageReturn extends Exception {
  private final Component componentMessage;
  
  public MessageReturn(Component message) {
    this.componentMessage = message;
  }
  
  public Component getComponentMessage() {
    return componentMessage;
  }
}
