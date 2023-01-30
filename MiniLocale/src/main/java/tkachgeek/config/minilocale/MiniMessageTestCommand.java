package tkachgeek.config.minilocale;

import net.kyori.adventure.text.minimessage.MiniMessage;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.commands.command.arguments.spaced.SpacedStringArg;
import tkachgeek.tkachutils.messages.MessageReturn;

public class MiniMessageTestCommand {
  public static Command get() {
    return new Command("miniMessageTest").arguments(
       new ArgumentSet(new MiniMessageTestExecutor(), "*", new SpacedStringArg("text"))
    );
  }
  
  private static class MiniMessageTestExecutor extends Executor {
    @Override
    public void executeForPlayer() throws MessageReturn {
      throw new MessageReturn(MiniMessage.get().parse(argS(0)));
    }
  }
}
