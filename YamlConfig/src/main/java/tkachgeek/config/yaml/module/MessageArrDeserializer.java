package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import tkachgeek.config.minilocale.MessageArr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageArrDeserializer extends JsonDeserializer<MessageArr> {
  @Override
  public MessageArr deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectCodec codec = p.getCodec();
    JsonNode node = codec.readTree(p);
    
    if (node.has("message")) {
      node = node.get("message");
    }
    
    List<String> values = new ArrayList<>();
    
    for (JsonNode jsonNode : node) {
      values.add(jsonNode.asText());
    }
    
    return new MessageArr(values.toArray(new String[0]));
  }
}
