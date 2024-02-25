package tkachgeek.config.minilocale.newCommands;

import ru.cwcode.commands.Command;
import tkachgeek.config.minilocale.MiniMessageTestCommand;

/**
 * Use MiniMessageTestCommand instead
 */
@Deprecated
public class MiniMessageTestPaperCommand {
  public static Command get() {
    return MiniMessageTestCommand.get();
  }
}
