package tkachgeek.config.minilocale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import tkachgeek.config.minilocale.wrapper.adventure.MiniMessageWrapper;

import java.util.ArrayList;
import java.util.List;

public class ItemData {
  @JsonProperty("материал")
  public Material material;
  @JsonProperty("название")
  String name;
  @JsonProperty("описание")
  String[] description;
  
  public ItemData() {
  }
  
  public ItemData(Material material, String name, String... description) {
    this.material = material;
    this.name = name;
    this.description = description;
  }
  
  @JsonIgnore
  public Component getName() {
    return MiniMessageWrapper.deserialize(name);
  }
  
  @JsonIgnore
  public Component getName(Placeholders placeholders) {
    return MiniMessageWrapper.deserialize(name, placeholders);
  }
  
  @JsonIgnore
  public List<Component> getDescription() {
    List<Component> list = new ArrayList<>();
    for (String line : description) {
      list.add(MiniMessageWrapper.deserialize(line).decoration(TextDecoration.ITALIC, false));
    }
    return list;
  }
  
  @JsonIgnore
  public List<Component> getDescription(Placeholders placeholders) {
    List<Component> list = new ArrayList<>();
    for (String line : description) {
      list.add(MiniMessageWrapper.deserialize(line, placeholders));
    }
    return list;
  }
}
