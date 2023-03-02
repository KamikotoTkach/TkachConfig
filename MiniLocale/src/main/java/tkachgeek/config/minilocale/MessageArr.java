package tkachgeek.config.minilocale;

import tkachgeek.tkachutils.collections.CollectionUtils;

public class MessageArr extends Message {
  public MessageArr() {
    super();
  }
  
  public MessageArr(String... message) {
    super(CollectionUtils.toString(message, "", "\n", true));
  }
  
  public MessageArr(Mode mode, String... message) {
    super(CollectionUtils.toString(message, "", "\n", true), mode);
  }
}
