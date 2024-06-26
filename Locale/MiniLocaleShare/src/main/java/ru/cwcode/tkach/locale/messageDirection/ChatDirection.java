package ru.cwcode.tkach.locale.messageDirection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class ChatDirection extends MessageDirection {

  public static final ChatDirection INSTANCE = new ChatDirection();

  private ChatDirection() {
  }

  @Override
  public void send(Audience audience, Component message) {
    if (message == null) return;

    audience.sendMessage(message);
  }
}
