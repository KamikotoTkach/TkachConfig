package ru.cwcode.tkach.config.paper.jackson.modules;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;

public class EnchantmentDeserializer extends JsonDeserializer<Enchantment> {
  
  @Override
  public Enchantment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return Enchantment.getByName(p.getValueAsString());
  }
}