package tkachgeek.config.yaml.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SoundData implements Serializable {
  private final String name;
  private final String sound;
  private final float volume;
  private final float pitch;

  @JsonCreator
  public SoundData(
        @JsonProperty("name") @NotNull final String name,
        @JsonProperty("sound") @NotNull final String sound,
        @JsonProperty("volume") final float volume,
        @JsonProperty("pitch") final float pitch
  ) {
    this.name = name;
    this.sound = sound;
    this.volume = volume;
    this.pitch = pitch;
  }

  public String getName() {
    return this.name;
  }

  public Sound.Builder getSoundBuilder() {
    return Sound.sound()
                .type(Key.key(this.sound))
                .volume(volume)
                .pitch(pitch);
  }

  public void play(@NotNull final Player player) {
    player.playSound(this.getSoundBuilder().build());
  }

  public void play(@NotNull final Location location) {
    location.getWorld().playSound(this.getSoundBuilder().build());
  }
}
