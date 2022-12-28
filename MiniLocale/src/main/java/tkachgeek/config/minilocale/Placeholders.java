package tkachgeek.config.minilocale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

import java.util.ArrayList;
import java.util.List;

public class Placeholders {
    private final List<Template> resolvers = new ArrayList<>();

    public Placeholders(String key, String value) {
        add(key, value);
    }
    
    public Placeholders(Template resolver) {
        add(resolver);
    }
    
    public Placeholders add(String key, String value) {
        resolvers.add(Template.of(key, value));
        return this;
    }
    
    public Placeholders add(Template TagResolver) {
        resolvers.add(TagResolver);
        return this;
    }
    
    public Placeholders add(String key, Component value) {
        resolvers.add(Template.of(key, value));
        return this;
    }
    
    public List<Template> getResolvers() {
        return resolvers;
    }
}
