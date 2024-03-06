package tkachgeek.config.minilocale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class ActionBarDirection extends MessageDirection {
  public static final ActionBarDirection INSTANCE = new ActionBarDirection();
  
  private ActionBarDirection() {
  }
  
  @Override
  public void send(Audience audience, Component message) {
    if (message == null) return;
    
    audience.sendActionBar(message);
  }
}
