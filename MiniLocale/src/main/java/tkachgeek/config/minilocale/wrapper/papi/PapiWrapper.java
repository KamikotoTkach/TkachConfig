package tkachgeek.config.minilocale.wrapper.papi;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;

public class PapiWrapper {
  public static boolean isPapiLoaded() {
    return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
  }
  
  public static String process(String text, Audience receiver) {
    return isPapiLoaded() ? PapiProcessor.process(text, receiver) : text;
  }
}
