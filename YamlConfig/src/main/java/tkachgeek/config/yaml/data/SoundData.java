package tkachgeek.config.yaml.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tkachgeek.tkachutils.sound.SoundUtils;

import java.io.Serializable;

public class SoundData implements Serializable {
  private final String sound;
  private final float volume;
  private final float pitch;

  @JsonCreator
  public SoundData(
        @JsonProperty("sound") @NotNull final String sound,
        @JsonProperty("volume") final float volume,
        @JsonProperty("pitch") final float pitch
  ) {
    this.sound = sound;
    this.volume = volume;
    this.pitch = pitch;
  }

  public String getSound() {
    return sound;
  }

  public Sound sound(@NotNull final Sound.Source source) {
    return Sound.sound(Key.key(this.sound), source, this.volume, this.pitch);
  }

  public Sound.Builder builder() {
    return Sound.sound()
                .type(Key.key(this.sound))
                .volume(this.volume)
                .pitch(this.pitch);
  }

  public void play(@NotNull final Player player) {
    player.playSound(this.builder().build());
  }

  public void play(@NotNull final Location location) {
    location.getWorld().playSound(location, SoundUtils.parse(this.sound), this.volume, this.pitch);
  }
}
