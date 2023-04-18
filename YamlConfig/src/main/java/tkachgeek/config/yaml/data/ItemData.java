package tkachgeek.config.yaml.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.MessageArr;
import tkachgeek.config.minilocale.Placeholders;
import tkachgeek.tkachutils.items.ItemBuilder;

public class ItemData {
  public Material material;
  Message name;
  MessageArr description;
  
  public ItemData() {
  }
  
  public ItemData(Material material, String name, String description) {
    this.material = material;
    this.name = new Message(name);
    this.description = new MessageArr(description);
  }
  
  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  public Message getName() {
    return name;
  }
  
  public void setName(Message name) {
    this.name = name;
  }
  
  public MessageArr getDescription() {
    return description;
  }
  
  public void setDescription(MessageArr description) {
    this.description = description;
  }
  
  public ItemBuilder getItemBuilder() {
    return ItemBuilder.of(material)
                      .description(description.get().toArray(new Component[0]))
                      .name(name.get());
  }
  
  public ItemBuilder getItemBuilder(Placeholders placeholders) {
    return ItemBuilder.of(material)
                      .description(description.get(placeholders).toArray(new Component[0]))
                      .name(name.get(placeholders));
  }
}
