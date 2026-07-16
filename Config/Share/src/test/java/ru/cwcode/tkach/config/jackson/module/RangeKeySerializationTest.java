package ru.cwcode.tkach.config.jackson.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;
import ru.cwcode.tkach.config.data.range.DoubleRange;
import ru.cwcode.tkach.config.data.range.IntRange;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RangeKeySerializationTest {
    private final ObjectMapper mapper = createMapper();

    @Test
    public void deserializesStaticIntRange() throws Exception {
        Map<IntRange, String> ranges = mapper.readValue("5: value\n", new TypeReference<>() {});

        assertEquals("value", ranges.get(new IntRange(5, 5)));
    }

    @Test
    public void deserializesStaticDoubleRange() throws Exception {
        Map<DoubleRange, String> ranges = mapper.readValue("1.25: value\n", new TypeReference<>() {});

        assertEquals("value", ranges.get(new DoubleRange(1.25, 1.25)));
    }

    @Test
    public void keepsDeserializingBoundedRanges() throws Exception {
        Map<IntRange, String> intRanges = mapper.readValue("1..5: value\n", new TypeReference<>() {});
        Map<DoubleRange, String> doubleRanges = mapper.readValue("1.25..2.5: value\n", new TypeReference<>() {});

        assertEquals("value", intRanges.get(new IntRange(1, 5)));
        assertEquals("value", doubleRanges.get(new DoubleRange(1.25, 2.5)));
    }

    @Test
    public void serializesStaticRangesAsSingleNumbers() throws Exception {
        JsonNode intRange = mapper.readTree(mapper.writeValueAsString(Map.of(new IntRange(5, 5), "value")));
        JsonNode doubleRange = mapper.readTree(mapper.writeValueAsString(Map.of(new DoubleRange(1.25, 1.25), "value")));

        assertEquals("5", intRange.fieldNames().next());
        assertEquals("1.25", doubleRange.fieldNames().next());
    }

    private ObjectMapper createMapper() {
        SimpleModule module = new SimpleModule();
        module.addKeySerializer(IntRange.class, new IntRangeKeySerializer());
        module.addKeyDeserializer(IntRange.class, new IntRangeKeyDeserializer());
        module.addKeySerializer(DoubleRange.class, new DoubleRangeKeySerializer());
        module.addKeyDeserializer(DoubleRange.class, new DoubleRangeKeyDeserializer());

        return new ObjectMapper(new YAMLFactory()).registerModule(module);
    }
}
