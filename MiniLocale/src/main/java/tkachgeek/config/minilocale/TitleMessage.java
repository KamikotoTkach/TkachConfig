package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.time.Duration;

public class TitleMessage implements Serializable {
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
    showTitle(player, title.get(player), subtitle.get(player));
  }
  
  public void show(Player player, Placeholders placeholders) {
    showTitle(player, title.get(player, placeholders), subtitle.get(player, placeholders));
  }
  
  private void showTitle(Player player, Component title, Component subtitle) {
    player.showTitle(Title.title(title, subtitle, Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))));
  }
}
