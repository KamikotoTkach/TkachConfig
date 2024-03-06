package tkachgeek.config.minilocale.wrapper.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public class PapiProcessor {
  public static String process(String st, Audience receiver) {
    if (st == null) return null;
    
    if (receiver instanceof Player) {
      return PlaceholderAPI.setPlaceholders((Player) receiver, st);
    }
    return st;
  }
}
