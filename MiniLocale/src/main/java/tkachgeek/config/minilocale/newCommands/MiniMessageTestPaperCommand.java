package tkachgeek.config.minilocale.newCommands;

import org.bukkit.Bukkit;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.arguments.spaced.SpacedStringArg;
import ru.cwcode.commands.paperplatform.argument.OnlinePlayers;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.config.minilocale.Message;
import tkachgeek.tkachutils.messages.MessageReturn;

public class MiniMessageTestPaperCommand {
  public static Command get() {
    return new Command("miniMessageTest").arguments(
       new ArgumentSet(new MiniMessageTestExecutor(), "*", new OnlinePlayers(), new SpacedStringArg("text"))
    );
  }
  
  private static class MiniMessageTestExecutor extends Executor {
    @Override
    public void executeForPlayer() throws MessageReturn {
      sender.sendMessage(new Message(argS(1)).get(Bukkit.getPlayer(argS(0))));
    }
  }
}
