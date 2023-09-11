package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import tkachgeek.config.minilocale.Message;

import java.io.IOException;

public class MessageDeserializer extends JsonDeserializer<Message> {
  @Override
  public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectCodec codec = p.getCodec();
    JsonNode node = codec.readTree(p);
    
    if (node.has("message")) {
      return new Message(node.get("message").asText());
    }
    
    return new Message(node.asText());
  }
}
