package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.bukkit.inventory.ItemStack;
import tkachgeek.config.Fancy;
import tkachgeek.tkachutils.items.ItemStackUtils;

import java.io.IOException;

public class ItemStackSerializer extends JsonSerializer<ItemStack> implements ContextualSerializer {
  boolean fancy;
  
  @Override
  public void serialize(ItemStack value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (fancy) {
      gen.writeString(ItemStackUtils.toSNBT(value));
    } else {
      gen.writeBinary(value.serializeAsBytes());
    }
  }
  
  @Override
  public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
    fancy = property.getAnnotation(Fancy.class) != null;
    return this;
  }
}
