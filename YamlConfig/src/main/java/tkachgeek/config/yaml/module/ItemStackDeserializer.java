package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.inventory.ItemStack;
import tkachgeek.tkachutils.items.ItemStackUtils;

import java.io.IOException;

public class ItemStackDeserializer extends JsonDeserializer<ItemStack> {
  
  @Override
  public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.hasToken(JsonToken.VALUE_STRING)) return ItemStackUtils.fromSNBT(p.getValueAsString());
    
    return ItemStack.deserializeBytes(p.getBinaryValue());
  }
}
