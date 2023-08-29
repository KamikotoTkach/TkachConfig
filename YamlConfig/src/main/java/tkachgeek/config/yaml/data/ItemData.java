package tkachgeek.config.yaml.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import tkachgeek.config.minilocale.Message;
import tkachgeek.config.minilocale.MessageArr;
import tkachgeek.config.minilocale.Placeholders;
import tkachgeek.tkachutils.items.ItemBuilder;

import java.io.Serializable;

public class ItemData implements Serializable {
  Material material;
  Message name;
  MessageArr description;
  int customModelData = 0;
  
  public ItemData() {
  }
  
  public ItemData(Material material, Message name, MessageArr description, int customModelData) {
    this(material, name, customModelData);
    this.description = description;
  }
  
  public ItemData(Material material, Message name, int customModelData) {
    this(material, name);
    this.customModelData = customModelData;
  }
  
  public ItemData(Material material, Message name) {
    this.material = material;
    this.name = name;
  }
  
  public ItemData(Material material, String name, String description, int customModelData) {
    this(material, name, customModelData);
    this.description = new MessageArr(description.split("\\n"));
  }
  
  public ItemData(Material material, String name, int customModelData) {
    this(material, new Message(name), customModelData);
  }
  
  public ItemData(Material material, Message name, MessageArr description) {
    this(material, name);
    this.description = description;
  }
  
  public ItemData(Material material, String name, String description) {
    this(material, name);
    this.description = new MessageArr(description.split("\\n"));
  }
  
  public ItemData(Material material, String name) {
    this(material, new Message(name));
  }
  
  public int getCustomModelData() {
    return customModelData;
  }
  
  public void setCustomModelData(int customModelData) {
    this.customModelData = customModelData;
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
    
    if (name != null) builder.name(name.get());
    
    if (customModelData != 0) builder.customModelData(customModelData);
    
    if (description != null) builder.description(description.get().toArray(new Component[0]));
    
    return builder;
  }
  
  public ItemBuilder getItemBuilder(Placeholders placeholders) {
    ItemBuilder builder = ItemBuilder.of(material);
    
    if (name != null) builder.name(name.get(placeholders));
    
    if (customModelData != 0) builder.customModelData(customModelData);
    
    if (description != null) builder.description(description.get(placeholders).toArray(new Component[0]));
    
    return builder;
  }
}
