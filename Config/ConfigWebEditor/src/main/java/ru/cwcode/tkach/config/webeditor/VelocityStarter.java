package ru.cwcode.tkach.config.webeditor;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.VelocityL10nPlatform;

import java.nio.file.Path;

@Plugin(
  id = "cwconfig_webeditor",
  name = "CWConfig web editor",
  version = "2.7.2",
  dependencies = {@Dependency(id = "cwconfig")}
)
public class VelocityStarter {
  public static WebEditor INSTANCE;
  
  @Inject
  @DataDirectory
  Path dataDirectory;
  @Inject
  Logger logger;
  @Inject
  ProxyServer server;
  
  @Subscribe
  public void onEnable(ProxyInitializeEvent event) {
    INSTANCE = new WebEditor(new SimpleConfig("config", new VelocityL10nPlatform(this,
                                                                                dataDirectory,
                                                                                logger,
                                                                                server.getPluginManager()
                                                                                      .ensurePluginContainer(this)
                                                                                      .getDescription()
                                                                                      .getSource()
                                                                                      .orElseThrow()
                                                                                      .toFile())));
    INSTANCE.start();
  }
}
