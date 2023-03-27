package tkachgeek.config.yaml.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationDeserializer extends JsonDeserializer<Location> {
  @Override
  public Location deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String[] s = p.getValueAsString().split(", ");
    String world = s[0];
  
    String[] cords = s[1].split(" ");
  
    double x = Double.parseDouble(cords[0]);
    double y = Double.parseDouble(cords[1]);
    double z = Double.parseDouble(cords[2]);
  
    if (cords.length > 3) { //для совместимости со старой версией
    
      float pitch = Float.parseFloat(cords[3]);
      float yaw = Float.parseFloat(cords[4]);
    
      return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    } else {
    
      return new Location(Bukkit.getWorld(world), x, y, z);
    }
  }
}
