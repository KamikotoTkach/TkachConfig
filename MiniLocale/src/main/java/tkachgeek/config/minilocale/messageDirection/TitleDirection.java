package tkachgeek.config.minilocale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class TitleDirection extends MessageDirection {
  public static final TitleDirection INSTANCE = new TitleDirection();
  
  private TitleDirection() {
  }
  
  @Override
  public void send(Audience audience, Component message) {
    if (message == null) return;
    
    send(audience, message, Component.empty(), Title.DEFAULT_TIMES);
  }
  
  public void send(Audience audience, Component title, Component subtitle, Title.Times times) {
    audience.showTitle(Title.title(title, subtitle, times));
  }
}
