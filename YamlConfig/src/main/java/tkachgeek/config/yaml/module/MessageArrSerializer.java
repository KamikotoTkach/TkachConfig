package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tkachgeek.config.minilocale.MessageArr;

import java.io.IOException;
import java.util.List;

public class MessageArrSerializer extends JsonSerializer<MessageArr> {
  @Override
  public void serialize(MessageArr value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    List<String> list = value.toList();
    gen.writeArray(list.toArray(new String[0]), 0, list.size());
  }
}