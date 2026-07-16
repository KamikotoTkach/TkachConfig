package ru.cwcode.tkach.config.jackson.module;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ru.cwcode.tkach.config.data.range.IntRange;

import java.io.IOException;

public class IntRangeKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        String[] parts = key.split("\\.\\.", 2);
        try {
            if (parts.length == 1) {
                int value = Integer.parseInt(parts[0]);
                return new IntRange(value, value);
            }

            return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            throw invalidKey(ctxt, key);
        }
    }

    private JsonMappingException invalidKey(DeserializationContext ctxt, String key) {
        return ctxt.weirdKeyException(IntRange.class, key, "Expected IntRange key in '<value>' or '<min>..<max>' format");
    }
}
