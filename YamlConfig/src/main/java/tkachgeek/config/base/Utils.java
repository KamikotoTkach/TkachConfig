package tkachgeek.config.base;

import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Utils {
  public static String readString(Path path) {
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (IOException ignored) {
    }
    return Strings.EMPTY;
  }
  
  public static void writeString(Path path, String text) {
    try {
      if(!Files.exists(path)) {
        com.google.common.io.Files.createParentDirs(path.toFile());
      }
      
      Files.writeString(path, text, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static <T extends Config> T getNewInstance(Class<T> type) {
    try {
      Constructor<T> declaredConstructor = type.getDeclaredConstructor();
      declaredConstructor.setAccessible(true);
      return declaredConstructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
