package ru.cwcode.tkach.config.commands;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.tkach.config.annotation.Reloadable;
import ru.cwcode.tkach.config.base.Config;
import ru.cwcode.tkach.config.base.manager.ConfigManager;

import java.util.function.Consumer;

public class ReloadCommands {
  public static <C extends Config<C>> Command get(ConfigManager<C> configManager) {
    return get(configManager, c -> {});
  }
  
  public static <C extends Config<C>> Command get(ConfigManager<C> configManager, Consumer<C> reloadedConfigConsumer) {
    return new Command("config")
       .subCommands(
          new Command("reload")
             .arguments(
                new ArgumentSet(new ReloadCommand<>(configManager, reloadedConfigConsumer), new ConfigArg<>(Reloadable.class::isInstance, configManager)),
                new ArgumentSet(new ReloadAllCommand<>(configManager, reloadedConfigConsumer), new ExactStringArg("all").optional())
             ),
          new Command("save")
             .arguments(
                new ArgumentSet(new SaveCommand<>(configManager), new ConfigArg<>(x -> true, configManager)),
                new ArgumentSet(new SaveAllCommand<>(configManager), new ExactStringArg("all").optional())
             ),
          new Command("backup")
             .arguments(
                new ArgumentSet(new BackupCommand<>(configManager), new ConfigArg<>(x -> true, configManager)),
                new ArgumentSet(new BackupAllCommand<>(configManager), new ExactStringArg("all").optional())
             )
       );
  }
}
