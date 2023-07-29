package tkachgeek.config.yaml;

import net.kyori.adventure.text.Component;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StoreCommand {
  public static Command get(YmlConfigManager manager) {
    return new Command("saveAll", "$*").arguments(
       new ArgumentSet(new SaveAll(manager), "")
    );
  }
  
  private static class SaveAll extends Executor {
    YmlConfigManager manager;
    
    public SaveAll(YmlConfigManager manager) {
      this.manager = manager;
    }
    
    @Override
    public void executeForPlayer() throws MessageReturn {
      Logger.getLogger(sender().getName()).log(Level.INFO, "Инициировал сохранение конфига " + argS(0));
      manager.storeAll();
      sender.sendMessage(Component.text("Готово"));
    }
  }
}
