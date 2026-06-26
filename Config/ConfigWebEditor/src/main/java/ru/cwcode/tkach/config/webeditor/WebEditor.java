package ru.cwcode.tkach.config.webeditor;

import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.webeditor.http.WebEditorServer;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService;
import ru.cwcode.tkach.config.webeditor.service.RawFileService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WebEditor {
  private final List<BiConsumer<YmlConfig, YmlConfig>> reloadListeners = new ArrayList<>();
  private final int port;
  private final Path pluginsRoot;
  private WebEditorServer server;
  
  public WebEditor(SimpleConfig config, Path pluginsRoot) {
    port = config.get("port", int.class, 2025);
    this.pluginsRoot = pluginsRoot;
  }
  
  public void addReloadListener(BiConsumer<YmlConfig, YmlConfig> action) {
    reloadListeners.add(action);
  }
  
  public void start() {
    server = new WebEditorServer(port, new ConfigEditorService(reloadListeners), new RawFileService(pluginsRoot));
    server.start();
  }
  
  public void stop() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }
}
