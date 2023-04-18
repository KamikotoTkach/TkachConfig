package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tkachgeek.config.minilocale.Message;

import java.io.IOException;

public class MessageSerializer extends JsonSerializer<Message> {
  @Override
  public void serialize(Message value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeString(value.serialize());
  }
}
