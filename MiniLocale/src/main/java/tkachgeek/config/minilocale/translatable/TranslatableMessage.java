package tkachgeek.config.minilocale.translatable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.Mode;
import tkachgeek.config.minilocale.Placeholders;
import tkachgeek.config.minilocale.wrapper.adventure.MiniMessageWrapper;
import tkachgeek.config.minilocale.wrapper.papi.PapiWrapper;

import java.util.HashMap;

public class TranslatableMessage extends Message {
  HashMap<String, String> translates = new HashMap<>();
  
  public TranslatableMessage() {
    super();
  }
  
  public TranslatableMessage(String... message) {
    super(message);
  }
  
  public TranslatableMessage(Mode mode, String... message) {
    super(mode, message);
  }
  
  public TranslatableMessage(String message) {
    super(message);
  }
  
  public TranslatableMessage(String message, Mode mode) {
    super(message, mode);
  }
  
  public HashMap<String, String> getTranslates() {
    return translates;
  }
  
  public void setTranslates(HashMap<String, String> translates) {
    this.translates = translates;
  }
  
  @Override
  public Component get(Placeholders placeholders, Audience receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(getMessage(receiver), receiver), placeholders);
  }
  
  @Override
  public Component get(Audience receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(getMessage(receiver), receiver));
  }
  
  private String getMessage(Audience receiver) {
    if (receiver instanceof Player) {
      String playerLocale = ((Player) receiver).locale().getLanguage();
      if (translates.containsKey(playerLocale)) {
        return translates.get(playerLocale);
      }
    }
    return message;
  }
}
