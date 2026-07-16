package ru.cwcode.tkach.config.jackson.module;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ru.cwcode.tkach.config.data.range.DoubleRange;

import java.io.IOException;

public class DoubleRangeKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        String[] parts = key.split("\\.\\.", 2);
        try {
            if (parts.length == 1) {
                double value = Double.parseDouble(parts[0]);
                return new DoubleRange(value, value);
            }

            return new DoubleRange(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        } catch (NumberFormatException e) {
            throw invalidKey(ctxt, key);
        }
    }

    private JsonMappingException invalidKey(DeserializationContext ctxt, String key) {
        return ctxt.weirdKeyException(DoubleRange.class, key, "Expected DoubleRange key in '<value>' or '<min>..<max>' format");
    }
}
