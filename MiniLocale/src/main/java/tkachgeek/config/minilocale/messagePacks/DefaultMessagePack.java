package tkachgeek.config.minilocale.messagePacks;

import tkachgeek.config.minilocale.Message;

public class DefaultMessagePack {
  public Message you_dont_have_permissions = new Message("<gold>У вас нет прав на это");
  public Message done = new Message("<green>Готово");
  public Message $player_not_found = new Message("<gold>Игрок <yellow><player> <gold>не найден");
  public Message $player_not_online = new Message("<gold>Игрок <yellow><player> <gold>не в сети");
  public Message $name_is_using = new Message("<yellow><name> <gold>уже используется.");
  public Message for_console_only = new Message("<gold>Можно вызвать только с консоли");
  public Message for_player_only = new Message("<gold>Может вызвать только игрок");
  public Message no_item_in_main_hand = new Message("<gold>Возьмите предмет в руку");
  
  public DefaultMessagePack() {
  }
}
