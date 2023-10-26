package tkachgeek.config.yaml.newCommands;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.config.yaml.YmlConfigManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PaperReloadCommand {
  public static Command get(YmlConfigManager manager) {
    return new Command("reload", "$*").arguments(
       new ArgumentSet(new ConfigReload(manager), "", new PaperConfigsArg(manager)),
       new ArgumentSet(new ConfigReloadAll(manager), "", new ExactStringArg("all"))
    );
  }
  
  public static class ConfigReload extends Executor {
    protected YmlConfigManager manager;
    
    public ConfigReload(YmlConfigManager manager) {
      this.manager = manager;
    }
    
    @Override
    public void executeForPlayer() {
      Audience audience = sender();
      
      if (audience instanceof CommandSender) {
        Logger.getLogger(((CommandSender) audience).getName()).log(Level.INFO, "Инициировал перезагрузку конфига " + argS(0));
      }
      
      manager.reloadByCommand(argS(0), audience);
    }
  }
  
  public static class ConfigReloadAll extends ConfigReload {
    public ConfigReloadAll(YmlConfigManager manager) {
      super(manager);
    }
    
    @Override
    public void executeForPlayer() {
      Audience audience = sender.getAudience();
      
      if (audience instanceof CommandSender) {
        Logger.getLogger(((CommandSender) audience).getName()).log(Level.INFO, "Инициировал перезагрузку конфигов");
      }
      manager.reloadByCommand(audience);
    }
  }
}
