package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

public class Placeholder {
    public static Placeholders add(String key, String value) {
        return new Placeholders(Template.of(key, value));
    }
    
    public static Placeholders add(String key, Component value) {
        return new Placeholders(Template.of(key, value));
    }
    
    public static Placeholders add(Template resolver) {
        return new Placeholders(resolver);
    }
    
    public static Placeholders add(String key, double value) {
        return add(key, String.valueOf(value));
    }
    
    public static Placeholders add(String key, int value) {
        return add(key, String.valueOf(value));
    }
    
    public static Placeholders add(String key, float value) {
        return add(key, String.valueOf(value));
    }
    
    public static Placeholders add(String key, long value) {
        return add(key, String.valueOf(value));
    }
    
    public static Placeholders add(String key, boolean value) {
        return add(key, String.valueOf(value));
    }
}
