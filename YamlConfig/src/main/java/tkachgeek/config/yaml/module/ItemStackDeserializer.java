package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.inventory.ItemStack;
import tkachgeek.tkachutils.items.ItemStackUtils;

import java.io.IOException;
import java.util.Base64;

public class ItemStackDeserializer extends JsonDeserializer<ItemStack> {
  
  @Override
  public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.hasToken(JsonToken.VALUE_STRING)) {
      String str = p.getValueAsString();
      
      if (!str.contains("{")) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(str));
      }
      
      return ItemStackUtils.fromSNBT(str);
    }
    
    return ItemStack.deserializeBytes(p.getBinaryValue());
  }
}
