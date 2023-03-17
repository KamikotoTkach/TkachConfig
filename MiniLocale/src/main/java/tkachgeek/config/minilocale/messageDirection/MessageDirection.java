package tkachgeek.config.minilocale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public abstract class MessageDirection {
  
  public abstract void send(Audience audience, Component message);
}
