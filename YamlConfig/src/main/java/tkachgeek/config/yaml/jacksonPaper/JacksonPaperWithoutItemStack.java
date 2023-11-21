package tkachgeek.config.yaml.jacksonPaper;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import de.eldoria.jacksonbukkit.JacksonBukkitModule;
import de.eldoria.jacksonbukkit.deserializer.ComponentGsonDeserializer;
import de.eldoria.jacksonbukkit.deserializer.ComponentMiniMessageDeserializer;
import de.eldoria.jacksonbukkit.deserializer.HexRGBAColorDeserializer;
import de.eldoria.jacksonbukkit.deserializer.RGBAColorDeserializer;
import de.eldoria.jacksonbukkit.serializer.ComponentGsonSerializer;
import de.eldoria.jacksonbukkit.serializer.ComponentMiniMessageSerializer;
import de.eldoria.jacksonbukkit.serializer.HexPaperColorSerializer;
import de.eldoria.jacksonbukkit.serializer.PaperColorSerializer;
import de.eldoria.jacksonbukkit.util.PaperFeatures;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public class JacksonPaperWithoutItemStack extends JacksonBukkitModule {
  private @Nullable JsonDeserializer<Component> componentDeserializer;
  private @Nullable JsonSerializer<Component> componentSerializer;
  
  public JacksonPaperWithoutItemStack() {
    super(true);
    
    if (PaperFeatures.HAS_MINI_MESSAGE) {
      componentDeserializer = new ComponentMiniMessageDeserializer();
      componentSerializer = new ComponentMiniMessageSerializer();
    } else if (PaperFeatures.HAS_ADVENTURE) {
      componentSerializer = new ComponentGsonSerializer();
      componentDeserializer = new ComponentGsonDeserializer();
    }
  }
  
  @Override
  public String getModuleName() {
    return "JacksonPaper";
  }
  
  @Override
  protected void registerSerializer(SimpleSerializers serializers) {
    serializers.addSerializer(Color.class, hexColors ? new HexPaperColorSerializer() : new PaperColorSerializer());
    if (componentSerializer != null) serializers.addSerializer(Component.class, componentSerializer);
  }
  
  @Override
  protected void registerDeserializer(SimpleDeserializers deserializers) {
    deserializers.addDeserializer(Color.class, hexColors ? new HexRGBAColorDeserializer() : new RGBAColorDeserializer());
    if (componentDeserializer != null) deserializers.addDeserializer(Component.class, componentDeserializer);
  }
}
