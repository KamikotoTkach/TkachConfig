package tkachgeek.config.minilocale;

import org.bukkit.Bukkit;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.bukkit.OnlinePlayers;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.commands.command.arguments.spaced.SpacedStringArg;
import tkachgeek.tkachutils.messages.MessageReturn;

public class MiniMessageTestCommand {
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
