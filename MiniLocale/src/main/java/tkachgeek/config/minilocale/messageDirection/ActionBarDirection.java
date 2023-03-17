package tkachgeek.config.minilocale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class ActionBarDirection extends MessageDirection {
  @Override
  public void send(Audience audience, Component message) {
    audience.sendActionBar(message);
  }
}
