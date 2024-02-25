package tkachgeek.config.minilocale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.config.minilocale.messageDirection.*;
import tkachgeek.config.minilocale.wrapper.adventure.AudienceWrapper;
import tkachgeek.config.minilocale.wrapper.adventure.MiniMessageWrapper;
import tkachgeek.config.minilocale.wrapper.papi.PapiWrapper;
import tkachgeek.tkachutils.collections.CollectionUtils;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;
import tkachgeek.tkachutils.text.component.LegacyComponentUtil;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

public class Message implements Serializable {
  public static final Pattern LEGACY_AMPERSAND = Pattern.compile("&[\\d#abcdefklmnrx]");
  public static final Pattern LEGACY_SECTION = Pattern.compile("§[\\d#abcdefklmnrx]");
  protected String message;
  
  //region constructors
  public Message() {
  }

  public Message(String... message) {
    this(CollectionUtils.toString(message, "", "\n", true));
  }

  public Message(String message) {
    this.message = message;
  }

  public Message(Component message) {
    this.message = LegacyComponentSerializer.legacySection().serialize(message);
  }

  public Message(Mode mode, String... message) {
    this(CollectionUtils.toString(message, "", "\n", true), mode);
  }

  public Message(String message, Mode mode) {
    this.message = message;

    switch (mode) {
      case MINI_MESSAGE:
        break;
      case LEGACY_AMPERSAND:
        this.message = MiniMessageWrapper.serialize(LegacyComponentUtil.parse(message)).replaceAll("\\\\", "");
        break;
      case LEGACY_SECTION:
        this.message = MiniMessageWrapper.serialize((LegacyComponentSerializer.legacySection().deserialize(message))).replaceAll("\\\\", "");
        break;
    }
  }
  //endregion
  
  //region base send methods
  public void send(MessageDirection direction, Audience audience) {
    audience.forEachAudience(x -> {
      if (x instanceof CommandSender) {
        direction.send(x, get((CommandSender) x));
      } else {
        direction.send(x, get());
      }
    });
  }
  
  public void send(MessageDirection direction, Iterable<? extends Audience> audiences) {
    for (Audience audience : audiences) {
      audience.forEachAudience(item -> {
        if (audience instanceof CommandSender) {
          direction.send(item, get((CommandSender) item));
        } else {
          direction.send(item, get());
        }
      });
    }
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
  
  public void send(MessageDirection direction, Iterable<? extends Audience> audiences, Placeholders placeholders) {
    for (Audience audience : audiences) {
      audience.forEachAudience(item -> {
        if (audience instanceof CommandSender) {
          direction.send(item, get(placeholders, (CommandSender) item));
        } else {
          direction.send(item, get(placeholders));
        }
      });
    }
  }
  //endregion
  
  //region get as component
  public Component get() {
    return MiniMessageWrapper.deserialize(message);
  }
  
  public Component get(Placeholders placeholders) {
    return MiniMessageWrapper.deserialize(message, placeholders);
  }
  
  public Component get(CommandSender receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(message, receiver));
  }
  
  public Component get(Placeholders placeholders, CommandSender receiver) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(message, receiver), placeholders);
  }
  
  public Component get(CommandSender receiver, Placeholders placeholders) {
    return MiniMessageWrapper.deserialize(PapiWrapper.process(message, receiver), placeholders);
  }
  //endregion
  
  //region send
  public void send(Audience audience) {
    send(ChatDirection.INSTANCE, audience);
  }
  
  public void send(Audience audience, Placeholders placeholders) {
    send(ChatDirection.INSTANCE, audience, placeholders);
  }
  
  public void send(UUID maybeOfflinePlayer) {
    if (Bukkit.getPlayer(maybeOfflinePlayer) != null) {
      send(Bukkit.getPlayer(maybeOfflinePlayer));
    }
  }
  
  public void send(UUID maybeOfflinePlayer, Placeholders placeholders) {
    if (Bukkit.getPlayer(maybeOfflinePlayer) != null) {
      send(Bukkit.getPlayer(maybeOfflinePlayer), placeholders);
    }
  }
  
  public void send(String playerName) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      send(player);
    }
  }
  
  public void send(String playerName, Placeholders placeholders) {
    Player player = Bukkit.getPlayer(playerName);
    if (player != null) {
      send(player, placeholders);
    }
  }
  
  public void send(Iterable<String> playerNames) {
    for (String playerName : playerNames) {
      send(playerName);
    }
  }
  
  public void send(Iterable<String> playerNames, Placeholders placeholders) {
    for (String playerName : playerNames) {
      send(playerName, placeholders);
    }
  }
  //endregion
  
  //region title
  public void sendTitle(Audience audience) {
    send(TitleDirection.INSTANCE, audience);
  }
  
  public void sendTitle(Audience audience, Placeholders placeholders) {
    send(TitleDirection.INSTANCE, audience, placeholders);
  }
  
  public void sendTitle(Iterable<? extends Audience> audiences) {
    send(TitleDirection.INSTANCE, audiences);
  }
  
  public void sendTitle(Iterable<? extends Audience> audiences, Placeholders placeholders) {
    send(TitleDirection.INSTANCE, audiences, placeholders);
  }
  //endregion
  
  //region subtitle
  public void sendSubtitle(Audience audience) {
    send(SubtitleDirection.INSTANCE, audience);
  }
  
  public void sendSubtitle(Audience audience, Placeholders placeholders) {
    send(SubtitleDirection.INSTANCE, audience, placeholders);
  }
  
  public void sendSubtitle(Iterable<? extends Audience> audiences) {
    send(SubtitleDirection.INSTANCE, audiences);
  }
  
  public void sendSubtitle(Iterable<? extends Audience> audiences, Placeholders placeholders) {
    send(SubtitleDirection.INSTANCE, audiences, placeholders);
  }
  //endregion
  
  //region broadcast
  public void broadcast() {
    send(ChatDirection.INSTANCE, AudienceWrapper.onlinePlayers());
  }
  
  public void broadcast(MessageDirection direction) {
    send(direction, AudienceWrapper.onlinePlayers());
  }
  
  public void broadcast(MessageDirection direction, Placeholders placeholders) {
    send(direction, AudienceWrapper.onlinePlayers(), placeholders);
  }
  //endregion
  
  //region action bar
  public void sendActionBar(Audience audience) {
    send(ActionBarDirection.INSTANCE, audience);
  }
  
  public void sendActionBar(Audience audience, Placeholders placeholders) {
    send(ActionBarDirection.INSTANCE, audience, placeholders);
  }
  
  public void sendActionBar(Iterable<? extends Audience> audiences) {
    send(ActionBarDirection.INSTANCE, audiences);
  }
  
  public void sendActionBar(Iterable<? extends Audience> audiences, Placeholders placeholders) {
    send(ActionBarDirection.INSTANCE, audiences, placeholders);
  }
  //endregion
  
  //region console
  public void console() {
    send(ChatDirection.INSTANCE, Bukkit.getConsoleSender());
  }
  
  public void console(Placeholders placeholders) {
    send(ChatDirection.INSTANCE, Bukkit.getConsoleSender(), placeholders);
  }
  //endregion
  
  //region get as string
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
  
  public String getText() {
    return PlainTextComponentSerializer.plainText().serialize(get());
  }
  
  public String getText(CommandSender receiver) {
    return PlainTextComponentSerializer.plainText().serialize(get(receiver));
  }
  
  public String getText(Placeholders placeholders) {
    return PlainTextComponentSerializer.plainText().serialize(get(placeholders));
  }
  
  public String getText(Placeholders placeholders, CommandSender receiver) {
    return PlainTextComponentSerializer.plainText().serialize(get(placeholders, receiver));
  }
  
  public String serialize() {
    return message;
  }
  //endregion
  
  //region throwback
  public void throwback() throws TargetableMessageReturn {
    throw new TargetableMessageReturn(this::get);
  }
  
  public void throwback(Placeholders placeholders) throws TargetableMessageReturn {
    throw new TargetableMessageReturn(receiver -> get(placeholders, receiver));
  }
  //endregion
  
  //region checks
  public boolean isNotEmpty() {
    return !message.isEmpty();
  }
  
  public boolean isEmpty() {
    return message.isEmpty();
  }
  //endregion
  
  //region utils
  public static Message parse(String message) {
    Mode mode;
    if (isAmpersand(message)) {
      mode = Mode.LEGACY_AMPERSAND;
    } else if (isSection(message)) {
      mode = Mode.LEGACY_SECTION;
    } else {
      mode = Mode.MINI_MESSAGE;
    }
    
    return new Message(message, mode);
  }

  private static boolean isAmpersand(String message) {
    return LEGACY_AMPERSAND.matcher(message).find();
  }

  private static boolean isSection(String message) {
    return LEGACY_SECTION.matcher(message).find();
  }
  //endregion
}
