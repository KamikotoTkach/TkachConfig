package tkachgeek.config.yaml;

import tkachgeek.config.base.Config;

public abstract class YmlConfig extends Config {
  protected YmlConfigManager manager = null;
  @Override
  public void store(String path) {
    manager.store(path, this);
  }
  
  @Override
  public void store() {
    manager.store(path, this);
  }
  
  @Override
  public String toString() {
    return manager.toString(this);
  }
  
  public abstract void load(YmlConfigManager manager);
}
