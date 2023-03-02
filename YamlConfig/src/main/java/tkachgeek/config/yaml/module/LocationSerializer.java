package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Location;

import java.io.IOException;

public class LocationSerializer extends JsonSerializer<Location> {
  @Override
  public void serialize(Location value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    String builder =
       value.getWorld().getName() + ", " +
          value.getX() + " " +
          value.getY() + " " +
          value.getZ();
    gen.writeString(builder);
  }
}
