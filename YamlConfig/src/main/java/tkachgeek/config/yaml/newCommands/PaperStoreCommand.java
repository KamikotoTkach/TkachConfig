package tkachgeek.config.yaml.newCommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.config.yaml.YmlConfigManager;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PaperStoreCommand {
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
      Audience audience = sender.getAudience();
      
      if (audience instanceof CommandSender) {
        Logger.getLogger(((CommandSender) audience).getName()).log(Level.INFO, "Инициировал сохранение конфигов");
      }
      
      manager.storeAll();
      sender.sendMessage(Component.text("Готово"));
    }
  }
}
