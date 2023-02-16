package tkachgeek.config.json;

import tkachgeek.config.base.Config;
@Deprecated
public abstract class JsonConfig extends Config {
  @Override
  public void store(String path) {
    JsonConfigManager.store(path, this);
  }
  
  @Override
  public void store() {
    JsonConfigManager.store(path, this);
  }
  
  @Override
  public String toString() {
    return JsonConfigManager.toString(this);
  }
}
