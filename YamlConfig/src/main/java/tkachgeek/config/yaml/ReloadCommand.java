package tkachgeek.config.yaml;

import org.bukkit.command.CommandSender;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReloadCommand {
  public static Command get(YmlConfigManager manager) {
    return new Command("reload", "$*").arguments(
       new ArgumentSet(new ConfigReload(manager), "", new ConfigsArg(manager)),
       new ArgumentSet(new ConfigReloadAll(manager), "", new ExactStringArg("all"))
    );
  }
  
  private static class ConfigReload extends Executor {
    YmlConfigManager manager;
    
    public ConfigReload(YmlConfigManager manager) {
      this.manager = manager;
    }
    
    @Override
    public void executeForPlayer() throws MessageReturn {
      CommandSender sender = sender().getCommandSender();
      Logger.getLogger(sender.getName()).log(Level.INFO, "Инициировал перезагрузку конфига " + argS(0));
      
      manager.reloadByCommand(argS(0), sender);
    }
  }
  
  private static class ConfigReloadAll extends ConfigReload {
    public ConfigReloadAll(YmlConfigManager manager) {
      super(manager);
    }
    
    @Override
    public void executeForPlayer() throws MessageReturn {
      CommandSender sender = CommandSender sender = sender().getCommandSender();
      
      Logger.getLogger(sender.getName()).log(Level.INFO, "Инициировал перезагрузку конфигов");
      manager.reloadByCommand(sender);
    }
  }
}
