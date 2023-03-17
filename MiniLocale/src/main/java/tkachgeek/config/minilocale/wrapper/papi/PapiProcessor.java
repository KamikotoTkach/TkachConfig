package tkachgeek.config.minilocale.wrapper.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PapiProcessor {
  public static String process(String st, CommandSender receiver) {
    if (receiver instanceof Player) {
      return PlaceholderAPI.setPlaceholders((Player) receiver, st);
    }
    return st;
  }
}
