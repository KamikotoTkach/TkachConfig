package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.config.minilocale.messageDirection.ChatDirection;
import tkachgeek.config.minilocale.messageDirection.MessageDirection;
import tkachgeek.config.minilocale.wrapper.adventure.AudienceWrapper;
import tkachgeek.config.minilocale.wrapper.adventure.MiniMessageWrapper;
import tkachgeek.config.minilocale.wrapper.papi.PapiWrapper;
import tkachgeek.tkachutils.collections.CollectionUtils;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.Iterator;
import java.util.UUID;

public class Message {
  protected String message;
  
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
    audience.forEachAudience(x -> {
      if (x instanceof CommandSender) {
        direction.send(x, get((CommandSender) x));
      } else {
        direction.send(x, get());
      }
    });
  }
  
  public void send(MessageDirection direction, Audience audience, Placeholders placeholders) {
    audience.forEachAudience(x -> {
      if (x instanceof CommandSender) {
        direction.send(x, get(placeholders, (CommandSender) x));
      } else {
        direction.send(x, get(placeholders));
      }
    });
  }
  
  public void send(MessageDirection direction, Iterator<? extends Audience> audiences) {
    audiences.forEachRemaining(audience -> audience.forEachAudience(item -> {
      if (audience instanceof CommandSender) {
        direction.send(item, get((CommandSender) item));
      } else {
        direction.send(item, get());
      }
    }));
  }
  
  public void send(MessageDirection direction, Iterator<? extends Audience> audiences, Placeholders placeholders) {
    audiences.forEachRemaining(audience -> audience.forEachAudience(item -> {
      if (audience instanceof CommandSender) {
        direction.send(item, get(placeholders, (CommandSender) item));
      } else {
        direction.send(item, get(placeholders));
      }
    }));
  }
  
  public void broadcast(MessageDirection direction) {
    send(direction, AudienceWrapper.onlinePlayers());
  }
  
  public void broadcast() {
    send(ChatDirection.INSTANCE, AudienceWrapper.onlinePlayers());
  }
  
  public void send(Audience audience) {
    send(ChatDirection.INSTANCE, audience);
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    send(ChatDirection.INSTANCE, audience, placeholders);
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
  
  public String getText() {
    return ((TextComponent) get()).content();
  }
  
  public String serialize() {
    return message;
  }
}
