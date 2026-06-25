package ru.cwcode.tkach.config.webeditor;

import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.tkach.config.jackson.yaml.YmlConfig;
import ru.cwcode.tkach.config.webeditor.http.WebEditorServer;
import ru.cwcode.tkach.config.webeditor.service.ConfigEditorService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WebEditor {
  private final List<BiConsumer<YmlConfig, YmlConfig>> reloadListeners = new ArrayList<>();
  private final int port;
  private WebEditorServer server;
  
  public WebEditor(SimpleConfig config) {
    port = config.get("port", int.class, 2025);
  }
  
  public void addReloadListener(BiConsumer<YmlConfig, YmlConfig> action) {
    reloadListeners.add(action);
  }
  
  public void start() {
    server = new WebEditorServer(port, new ConfigEditorService(reloadListeners));
    server.start();
  }
  
  public void stop() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }
}
