package tkachgeek.config.minilocale;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MiniLocale {
  static private BukkitAudiences adventure;
  
  public static @NonNull BukkitAudiences adventure() {
    if (adventure == null) {
      throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
    }
    return adventure;
  }
  
  public static void enableCompabilityMode(JavaPlugin plugin) {
    if (Bukkit.getPluginManager().getPlugin("AdventureCompability") != null) {
      adventure = BukkitAudiences.create(plugin);
    }
  }
  
  public static boolean isCompabilityModeEnabled() {
    return adventure != null;
  }
}
