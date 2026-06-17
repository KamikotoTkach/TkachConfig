package ru.cwcode.tkach.config.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.cwcode.tkach.config.base.Config;
import ru.cwcode.tkach.config.base.ConfigPersistOptions;
import ru.cwcode.tkach.config.base.manager.ConfigManager;
import ru.cwcode.tkach.config.base.manager.ConfigMapper;
import ru.cwcode.tkach.config.data.range.DoubleRange;
import ru.cwcode.tkach.config.data.range.IntRange;
import ru.cwcode.tkach.config.jackson.module.*;
import ru.cwcode.tkach.locale.Message;
import ru.cwcode.tkach.locale.MessageArr;
import ru.cwcode.tkach.locale.translatable.TranslatableMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class JacksonConfigMapper<C extends Config<C>> extends ConfigMapper<C> {
  protected ObjectMapper mapper;
  Map<Object, Module> registeredModules = new HashMap<>();
  
  public JacksonConfigMapper() {
  }
  
  protected abstract ObjectMapper createObjectMapper();
  
  public ObjectMapper getMapper() {
    return mapper;
  }
  
  @Override
  public void setConfigManager(ConfigManager<C> configManager) {
    super.setConfigManager(configManager);
    
    configureObjectMapper(mapper = createObjectMapper());
  }
  
  @Override
  public <V extends C> MappingResult<V> map(String string, Class<V> configClass, ConfigPersistOptions persistOptions) {
    return new MappingResult<V>(null, null);
  }
  
  @Override
  public Optional<String> map(C config, ConfigPersistOptions persistOptions) {
    return Optional.empty();
  }
  
  public void module(Module module) {
    registeredModules.put(module.getModuleName(), module);
    mapper.registerModule(module);
  }
  
  public void configureObjectMapper(ObjectMapper mapper) {
    mapper.findAndRegisterModules();
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    mapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS,true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    
    SimpleModule module = new SimpleModule("cwconfigLocale");
    
    module.addDeserializer(TranslatableMessage.class, new TranslatableMessageDeserializer());
    module.addSerializer(TranslatableMessage.class, new TranslatableMessageSerializer());
    
    module.addDeserializer(Message.class, new MessageDeserializer());
    module.addSerializer(Message.class, new MessageSerializer());
    
    module.addDeserializer(MessageArr.class, new MessageArrDeserializer());
    module.addSerializer(MessageArr.class, new MessageArrSerializer());
    
    module.addKeySerializer(IntRange.class, new IntRangeKeySerializer());
    module.addKeyDeserializer(IntRange.class, new IntRangeKeyDeserializer());
    
    module.addKeySerializer(DoubleRange.class, new DoubleRangeKeySerializer());
    module.addKeyDeserializer(DoubleRange.class, new DoubleRangeKeyDeserializer());
    
    mapper.registerModule(module);
    mapper.registerModule(new JavaTimeModule());
    
    for (Module additionalJacksonModule : configManager.platform().additionalJacksonModules()) {
      mapper.registerModule(additionalJacksonModule);
    }
    
    for (Module additionalModule : registeredModules.values()) {
      mapper.registerModule(additionalModule);
    }
  }
}
