package ru.cwcode.tkach.config.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.cwcode.tkach.config.data.range.DoubleRange;

import java.io.IOException;

public class DoubleRangeKeySerializer extends JsonSerializer<DoubleRange> {
    @Override
    public void serialize(DoubleRange value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeFieldName(Double.compare(value.min(), value.max()) == 0
            ? Double.toString(value.min())
            : value.min() + ".." + value.max());
    }
}
