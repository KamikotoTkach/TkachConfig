# CWConfig 2.7.2

aka PaperConfig, VelocityConfig

Библиотека для простой работы с конфигами для Paper и Velocity на основе [Jackson](https://github.com/FasterXML/jackson)

Подключение:

* Paper:
  ```xml
  <dependency>
    <groupId>ru.cwcode.tkach.config</groupId>
    <artifactId>PaperConfig</artifactId>
    <version>2.7.2</version>
    <scope>provided</scope>
  </dependency>
  ```
* Velocity
  ```xml
  <dependency>
    <groupId>ru.cwcode.tkach.config</groupId>
    <artifactId>VelocityConfig</artifactId>
    <version>2.7.2</version>
    <scope>provided</scope>
  </dependency>
  ```

Использование

Добавить зависимость в плагин:
* Paper: CWConfig
* Velocity: cwconfig

Создать YmlConfigManager

* Paper:
  ```java
  new YmlConfigManager(new PaperPluginConfigPlatform(this))
  ```
* Velocity:
  ```java
  new YmlConfigManager(new VelocityPluginConfigPlatform(plugin, server, logger, dataDirectory));
  ```

Далее создать сам конфиг:

```java
public class Example extends YmlConfig {
  //поля
}
```

В принципе конфиг готов.

Требования к классу конфига:

* Пустой конструктор
* Хоть одно нестатическое поле

Загрузить его можно так:

```java
Example = yml.load("example", Example.class);
```

Метод load загружает файл example.yml и парсит его в класс Example. Если файла нет или не удалось спарсить, то создаёт
инстанс класса Example и сохраняет его в файл example.yml.

Работает с почти любыми типами и коллекциями из стандартной явы и с другими классами через
кастомный [сериализатор](https://github.com/KamikotoTkach/TkachConfig/blob/master/Config/PaperPlatform/src/main/java/ru/cwcode/tkach/config/paper/jackson/modules/LocationSerializer.java)/[десериализатор](https://github.com/KamikotoTkach/TkachConfig/blob/master/Config/PaperPlatform/src/main/java/ru/cwcode/tkach/config/paper/jackson/modules/LocationDeserializer.java)
для него, а свои классы проще всего сохранять через пустой конструктор или конструктор с
аннотацией [@JsonCreator](https://reflectoring.io/spring-jsoncreator/).

Пример конфига с использованием синглтона:

```java
public class Example extends YmlConfig implements Reloadable {
  static Config instance;

  //поля

  public Example() {
  }

  public static Example getInstance() {
    if (instance == null) load();
    return instance;
  }

  public static void load() {
    instance = SomeClass.yml.load("example", Example.class);
  }

  @Override
  public void reload() {
    load();
  }
}
```

Как можно заметить, этот конфиг `implements Reloadable` - конфиг можно
перезагружать [командами](https://github.com/KamikotoTkach/TkachCommands):

```java
new Command("someCommand")
  .subCommands(
    ReloadCommands.get(yml)
  )
```

и в `someCommand` появится подкоманда `config reload <configName>`

Полезно знать: [Аннотации Jackson](https://www.baeldung.com/jackson-annotations) (В
особенности [@JsonSetter](https://www.baeldung.com/jackson-annotations#4-jsonsetter), [@JsonGetter](https://www.baeldung.com/jackson-annotations#2-jsongetter), [аннотации для работы с полиморфизмом](https://www.baeldung.com/jackson-annotations#jackson-polymorphic-type-handling-annotations), [@JsonCreator](https://www.baeldung.com/jackson-annotations#1-jsoncreator), [отношения](https://www.baeldung.com/jackson-annotations#5-jsonmanagedreference-jsonbackreference))

# CwConfigEditor:
* Текстовый редактор на базе npm install monaco-yaml-prebuilt@1.0.2 с валидации схемы \
* Визуальный редактор на базе json-editor \
По дефолту порт 2025