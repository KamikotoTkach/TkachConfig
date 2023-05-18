package tkachgeek.config.yaml.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.MessageArr;
import tkachgeek.config.minilocale.Placeholders;
import tkachgeek.tkachutils.items.ItemBuilder;

public class ItemData {
  Material material;
  Message name;
  MessageArr description;
  int customModelData = 0;
  
  public ItemData() {
  }
  
  public ItemData(Material material, String name, String description) {
    this.material = material;
    this.name = new Message(name);
    this.description = new MessageArr(description);
  }
  
  public ItemData(Material material, Message name) {
    this.material = material;
    this.name = name;
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
    ItemBuilder builder = ItemBuilder.of(material);
    
    builder.name(name.get());
    builder.customModelData(customModelData);
    
    if (description != null) builder.description(description.get().toArray(new Component[0]));
    
    return builder;
  }
  
  public ItemBuilder getItemBuilder(Placeholders placeholders) {
    ItemBuilder builder = ItemBuilder.of(material);
    
    builder.name(name.get(placeholders));
    builder.customModelData(customModelData);
    
    if (description != null) builder.description(description.get(placeholders).toArray(new Component[0]));
    
    return builder;
  }
}
