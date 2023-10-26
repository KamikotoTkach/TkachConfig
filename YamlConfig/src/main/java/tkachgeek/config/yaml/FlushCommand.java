package tkachgeek.config.yaml;

import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class FlushCommand {
  public static Command get(YmlConfigManager manager) {
    return new Command("flush", "$*").arguments(
       new ArgumentSet(new ConfigFlush(manager), "", new ConfigsArg(manager))
    );
  }
  
  @Deprecated
  private static class ConfigFlush extends Executor {
    YmlConfigManager manager;
    
    public ConfigFlush(YmlConfigManager manager) {
      this.manager = manager;
    }
    
    @Override
    public void executeForPlayer() throws MessageReturn {
      Logger.getLogger(sender().getName()).log(Level.INFO, "Инициировал очистку конфига " + argS(0));
      
      manager.flush(argS(0), sender());
    }
  }
}
