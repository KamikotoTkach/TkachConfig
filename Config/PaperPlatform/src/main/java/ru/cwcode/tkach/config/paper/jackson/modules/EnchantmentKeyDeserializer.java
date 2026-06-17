package ru.cwcode.tkach.config.paper.jackson.modules;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;

public class EnchantmentKeyDeserializer extends KeyDeserializer {
  @Override
  public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
    return Enchantment.getByName(key);
  }
}
