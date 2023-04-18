package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tkachgeek.config.minilocale.translatable.TranslatableMessage;

import java.io.IOException;

public class TranslatableMessageSerializer extends JsonSerializer<TranslatableMessage> {
  
  @Override
  public void serialize(TranslatableMessage value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    gen.writeFieldName("default");
    gen.writeString(value.getText());
    gen.writeFieldName("translates");
    serializers.defaultSerializeValue(value.getTranslates(), gen);
    gen.writeEndObject();
  }
}
