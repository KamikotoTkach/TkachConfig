package tkachgeek.config.base;

public abstract class Config {
  transient public String path;
  public transient boolean storeAllEnabled = true;
  
  void setStoreAllEnabled(boolean bool) {
    storeAllEnabled = bool;
  }
  
  abstract public void store();
  
  abstract public void store(String path);
  
  abstract public String toString();
}
