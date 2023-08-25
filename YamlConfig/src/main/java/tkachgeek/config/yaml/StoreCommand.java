package tkachgeek.config.yaml;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.paperplatform.executor.Executor;
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
      CommandSender sender = (CommandSender) sender();
      
      Logger.getLogger(sender.getName()).log(Level.INFO, "Инициировал сохранение конфига " + argS(0));
      manager.storeAll();
      sender.sendMessage(Component.text("Готово"));
    }
  }
}
