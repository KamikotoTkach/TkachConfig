package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.config.minilocale.messageDirection.MessageDirection;
import tkachgeek.config.minilocale.messageDirection.MessageDirections;
import tkachgeek.config.minilocale.wrapper.adventure.MiniMessageWrapper;
import tkachgeek.config.minilocale.wrapper.papi.PapiWrapper;
import tkachgeek.tkachutils.collections.CollectionUtils;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.UUID;

public class Message {
  String message;
  
  public Message() {
  }
  
  public Message(String... message) {
    this(CollectionUtils.toString(message, "", "\n", true));
  }
  
  public Message(Mode mode, String... message) {
    this(CollectionUtils.toString(message, "", "\n", true), mode);
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
  
  public void send(MessageDirection direction, Audience audience) {
    direction.send(audience, get());
  }
  
  public void send(MessageDirection direction, Audience audience, Placeholders placeholders) {
    direction.send(audience, get(placeholders));
  }
  
  public void send(Audience audience) {
    send(MessageDirections.CHAT, audience);
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    send(MessageDirections.CHAT, audience, placeholders);
  }
  
  public void send(UUID maybeOfflinePlayer, Placeholders placeholders) {
    if (Bukkit.getPlayer(maybeOfflinePlayer) != null) {
      send(Bukkit.getPlayer(maybeOfflinePlayer), placeholders);
    }
  }
  
  public void send(UUID maybeOfflinePlayer) {
    if (Bukkit.getPlayer(maybeOfflinePlayer) != null) {
      send(Bukkit.getPlayer(maybeOfflinePlayer));
    }
  }
  
  public void send(String playerName, Placeholders placeholders) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      send(player, placeholders);
    }
  }
  
  public void send(String playerName) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      send(player);
    }
  }
  
  public String getText() {
    return ((TextComponent) get()).content();
  }
  
  public Component get() {
    return MiniMessageWrapper.deserialize(message);
  }
  
  public Component get(CommandSender receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(message, receiver));
  }
  
  public Component get(Placeholders placeholders) {
    return MiniMessageWrapper.deserialize(message, placeholders);
  }
  
  public Component get(Placeholders placeholders, CommandSender receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(message, receiver), placeholders);
  }
  
  public String getLegacy() {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get());
  }
  
  public String getLegacy(Placeholders placeholders) {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get(placeholders));
  }
  
  public String getLegacy(CommandSender receiver) {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get(receiver));
  }
  
  public String getLegacy(Placeholders placeholders, CommandSender receiver) {
    return LegacyComponentSerializer.legacyAmpersand().serialize(get(placeholders, receiver));
  }
  
  public String getLegacySection() {
    return LegacyComponentSerializer.legacySection().serialize(get());
  }
  
  public String getLegacySection(Placeholders placeholders) {
    return LegacyComponentSerializer.legacySection().serialize(get(placeholders));
  }
  
  public String getLegacySection(CommandSender receiver) {
    return LegacyComponentSerializer.legacySection().serialize(get(receiver));
  }
  
  public String getLegacySection(Placeholders placeholders, CommandSender receiver) {
    return LegacyComponentSerializer.legacySection().serialize(get(placeholders, receiver));
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
