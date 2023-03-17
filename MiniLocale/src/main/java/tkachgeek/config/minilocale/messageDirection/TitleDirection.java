package tkachgeek.config.minilocale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class TitleDirection extends MessageDirection {
  @Override
  public void send(Audience audience, Component message) {
    send(audience, message, Component.empty(), Title.DEFAULT_TIMES);
  }
  
  public void send(Audience audience, Component title, Component subtitle, Title.Times times) {
    audience.showTitle(Title.title(title, subtitle, times));
  }
}
