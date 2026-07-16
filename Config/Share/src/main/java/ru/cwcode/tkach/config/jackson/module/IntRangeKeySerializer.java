package ru.cwcode.tkach.config.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.cwcode.tkach.config.data.range.IntRange;

import java.io.IOException;

public class IntRangeKeySerializer extends JsonSerializer<IntRange> {
    @Override
    public void serialize(IntRange value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeFieldName(value.min() == value.max()
            ? Integer.toString(value.min())
            : value.min() + ".." + value.max());
    }
}
