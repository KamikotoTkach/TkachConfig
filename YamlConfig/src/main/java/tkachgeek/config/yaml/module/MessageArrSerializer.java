package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tkachgeek.config.minilocale.MessageArr;

import java.io.IOException;

public class MessageArrSerializer extends JsonSerializer<MessageArr> {
  @Override
  public void serialize(MessageArr value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeStartArray();
    
    for (String s : value.toList()) {
      gen.writeString(s);
    }
    
    gen.writeEndArray();
  }
}