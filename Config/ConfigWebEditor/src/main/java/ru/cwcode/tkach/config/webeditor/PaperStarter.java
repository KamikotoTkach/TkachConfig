package ru.cwcode.tkach.config.webeditor;

import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.PaperL10nPlatform;

public class PaperStarter extends JavaPlugin {
  public static WebEditor INSTANCE;
  
  @Override
  public void onEnable() {
    INSTANCE = new WebEditor(new SimpleConfig("config", new PaperL10nPlatform(this, this.getFile())),
                             getDataFolder().getParentFile().toPath());
    
    INSTANCE.start();
  }
  
  @Override
  public void onDisable() {
    if (INSTANCE != null) {
      INSTANCE.stop();
      INSTANCE = null;
    }
  }
}
