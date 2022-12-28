package tkachgeek.config.minilocale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

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
    return MiniMessage.get().deserialize(name);
  }
  @JsonIgnore
  public Component getName(Placeholders placeholders) {
    return MiniMessage.get().parse(name, placeholders.getResolvers());
  }
  @JsonIgnore
  public List<Component> getDescription() {
    List<Component> list = new ArrayList<>();
    for (String line : description) {
      list.add(MiniMessage.get().deserialize(line).decoration(TextDecoration.ITALIC, false));
    }
    return list;
  }
  @JsonIgnore
  public List<Component> getDescription(Placeholders placeholders) {
    List<Component> list = new ArrayList<>();
    for (String line : description) {
      list.add(MiniMessage.get().parse(line, placeholders.getResolvers()));
    }
    return list;
  }
}
