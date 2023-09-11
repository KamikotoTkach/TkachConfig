package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import tkachgeek.config.minilocale.translatable.TranslatableMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class TranslatableMessageDeserializer extends JsonDeserializer<TranslatableMessage> {
  
  @Override
  public TranslatableMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String defaultText = node.get("default").asText();
    HashMap<String, String> translates = new HashMap<>();
    
    JsonNode translatesNode = node.get("translates");
    
    Iterator<String> fieldNames = translatesNode.fieldNames();
    
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      JsonNode fieldValue = translatesNode.get(fieldName);
      translates.put(fieldName, fieldValue.asText());
    }
    
    TranslatableMessage translatableMessage = new TranslatableMessage(defaultText);
    translatableMessage.setTranslates(translates);
    
    return translatableMessage;
  }
}
