package tkachgeek.config.yaml;

import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.ExactStringArg;
import tkachgeek.commands.command.arguments.executor.Executor;
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
      Logger.getLogger(sender().getName()).log(Level.INFO, "Инициировал перезагрузку конфига " + argS(0));
      
      manager.reloadByCommand(argS(0), sender());
    }
  }
  
  private static class ConfigReloadAll extends ConfigReload {
    public ConfigReloadAll(YmlConfigManager manager) {
      super(manager);
    }
    
    @Override
    public void executeForPlayer() throws MessageReturn {
      Logger.getLogger(sender().getName()).log(Level.INFO, "Инициировал перезагрузку конфигов");
      manager.reloadByCommand(sender());
    }
  }
}
