package tkachgeek.config.yaml;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.config.base.Config;
import tkachgeek.config.base.Reloadable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigsArg extends Argument {
  YmlConfigManager manager;
  
  public ConfigsArg(YmlConfigManager manager) {
    this.manager = manager;
  }
  
  @Override
  public boolean valid(String s) {
    for (Map.Entry<String, Config> x : manager.configs.entrySet()) {
      if (x.getValue() instanceof Reloadable && x.getKey().equals(s)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public List<String> completions(CommandSender commandSender) {
    List<String> list = new ArrayList<>();
    for (Map.Entry<String, Config> x : manager.configs.entrySet()) {
      if (x.getValue() instanceof Reloadable) {
        String key = x.getKey();
        list.add(key);
      }
    }
    return list;
  }
  
  @Override
  public String argumentName() {
    return "название конфига";
  }
}
