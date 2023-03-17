package tkachgeek.config.minilocale.wrapper.papi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PapiWrapper {
  public static boolean isPapiLoaded() {
    return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
  }
  
  public static String process(String text, CommandSender receiver) {
    return isPapiLoaded() ? PapiProcessor.process(text, receiver) : text;
  }
}
