package tkachgeek.config.minilocale;

import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class TitleMessage {
  Message title;
  Message subtitle;
  int fadeIn = 100;
  int stay = 1500;
  int fadeOut = 300;
  
  public TitleMessage() {
  }
  
  public TitleMessage(Message title, Message subtitle) {
    this.title = title;
    this.subtitle = subtitle;
  }
  
  public TitleMessage(String title, String subtitle) {
    this.title = new Message(title);
    this.subtitle = new Message(subtitle);
  }
  
  public void show(Player player) {
    player.showTitle(Title.title(title.get(player), subtitle.get(player), Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))));
  }
}
