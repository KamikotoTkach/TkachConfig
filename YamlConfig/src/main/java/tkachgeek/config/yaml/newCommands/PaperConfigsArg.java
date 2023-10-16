package tkachgeek.config.yaml.newCommands;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.spaced.SpacedArgument;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Reloadable;
import tkachgeek.config.yaml.YmlConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaperConfigsArg extends Argument implements SpacedArgument {
  YmlConfigManager manager;
  
  public PaperConfigsArg(YmlConfigManager manager) {
    this.manager = manager;
  }
  
  @Override
  public boolean valid(String name) {
    for (Map.Entry<String, Config> cfgEntry : manager.configs.entrySet()) {
      if (cfgEntry.getValue() instanceof Reloadable && cfgEntry.getKey().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public List<String> completions(Sender commandSender) {
    List<String> configList = new ArrayList<>();
    
    for (Map.Entry<String, Config> cfgEntry : manager.configs.entrySet()) {
      if (cfgEntry.getValue() instanceof Reloadable) {
        configList.add(cfgEntry.getKey());
      }
    }
    
    return configList;
  }
  
  @Override
  public String argumentName() {
    return "название конфига";
  }
}
