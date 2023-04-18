package tkachgeek.config.minilocale.wrapper.adventure;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AudienceWrapper {
  public static Audience wrap(Collection<UUID> pretenders, UUID... anotherPretenders) {
    Set<Player> players = new HashSet<>();
    
    addIfOnline(players, pretenders);
    addIfOnline(players, Arrays.asList(anotherPretenders));
    
    return Audience.audience(players);
  }
  
  public static Audience wrap(UUID... pretenders) {
    Set<Player> players = new HashSet<>();
    addIfOnline(players, Arrays.asList(pretenders));
    return Audience.audience(players);
  }
  
  public static Audience wrap(String... pretenders) {
    Set<Player> players = new HashSet<>();
    
    for (String pretender : pretenders) {
      Player player = Bukkit.getPlayer(pretender);
      if (player != null) {
        players.add(player);
      }
    }
    
    return Audience.audience(players);
  }
  
  /**
   * @param players    сет игроков
   * @param pretenders итерируемый список с UUID игроков, который будут добавлены в players, если этот игрок онлайн
   */
  public static void addIfOnline(Set<Player> players, Iterable<? extends UUID> pretenders) {
    for (UUID pretender : pretenders) {
      Player player = Bukkit.getPlayer(pretender);
      if (player != null) {
        players.add(player);
      }
    }
  }
  
  public static Audience onlinePlayers() {
    return Audience.audience(Bukkit.getOnlinePlayers());
  }
}
