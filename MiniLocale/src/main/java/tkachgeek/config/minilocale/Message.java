package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tkachgeek.config.minilocale.wrapper.MiniMessageWrapper;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.UUID;

public class Message {
  String message;
  
  public Message() {
  }
  
  public Message(String message) {
    this.message = message;
  }
  
  public Message(String message, Mode mode) {
    this.message = message;
    
    switch (mode) {
      case MINI_MESSAGE:
        break;
      case LEGACY_AMPERSAND:
        this.message = MiniMessageWrapper.serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message)).replaceAll("\\\\", "");
        break;
      case LEGACY_SECTION:
        this.message = MiniMessageWrapper.serialize((LegacyComponentSerializer.legacySection().deserialize(message))).replaceAll("\\\\", "");
        break;
    }
  }
  
  public void send(Audience audience) {
    audience.sendMessage(get());
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    audience.sendMessage(get(placeholders));
  }
  
  public void send(UUID maybeOfflinePlayer, Placeholders placeholders) {
    if (Bukkit.getOfflinePlayer(maybeOfflinePlayer).isOnline()) {
      Bukkit.getPlayer(maybeOfflinePlayer).sendMessage(get(placeholders));
    }
  }
  
  public void send(UUID maybeOfflinePlayer) {
    if (Bukkit.getOfflinePlayer(maybeOfflinePlayer).isOnline()) {
      Bukkit.getPlayer(maybeOfflinePlayer).sendMessage(get());
    }
  }
  
  public void send(String playerName, Placeholders placeholders) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      player.sendMessage(get(placeholders));
    }
  }
  
  public void send(String playerName) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      player.sendMessage(get());
    }
  }
  
  public String getText() {
    return LegacyComponentSerializer.legacySection().serialize(get());
  }
  
  public Component get() {
    return MiniMessageWrapper.deserialize(message);
  }
  
  public Component get(Placeholders placeholders) {
    return MiniMessageWrapper.deserialize(message, placeholders);
  }
  
  public String getLegacy() {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get());
  }
  
  public String getLegacy(Placeholders placeholders) {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get(placeholders));
  }
  
  public String getLegacySection() {
    return LegacyComponentSerializer.legacySection().serialize(get());
  }
  
  public String getLegacySection(Placeholders placeholders) {
    return LegacyComponentSerializer.legacySection().serialize(get(placeholders));
  }
  
  public void throwback() throws MessageReturn {
    throw new MessageReturn(get());
  }
  
  public void throwback(Placeholders placeholders) throws MessageReturn {
    throw new MessageReturn(get(placeholders));
  }
  
  public boolean isNotEmpty() {
    return !message.isEmpty();
  }
  
  public boolean isEmpty() {
    return message.isEmpty();
  }
}
