package ru.cwcode.tkach.config.velocityplatform;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import ru.cwcode.cwutils.l10n.L10n;
import ru.cwcode.cwutils.l10n.VelocityL10nPlatform;
import ru.cwcode.tkach.config.server.ServerPlatform;
import ru.cwcode.tkach.locale.platform.MiniLocale;
import ru.cwcode.tkach.locale.velocity.MiniLocaleVelocity;

import java.nio.file.Path;

@Plugin(
  id = "cwconfig",
  name = "CWConfig",
  version = "2.7.2",
  dependencies = {@Dependency(id = "cwcommands")}
)
public class VelocityPlatform {
  @Inject
  ProxyServer proxyServer;
  @Inject
  @DataDirectory
  Path dataDirectory;
  @Inject
  Logger logger;
  @Inject
  ProxyServer server;
  
  @Subscribe
  public void initialize(ProxyInitializeEvent event) {
    ServerPlatform.l10n = new L10n(new VelocityL10nPlatform(this,
                                                            dataDirectory,
                                                            logger,
                                                            server.getPluginManager()
                                                                  .ensurePluginContainer(this)
                                                                  .getDescription()
                                                                  .getSource()
                                                                  .orElseThrow()
                                                                  .toFile()));
    MiniLocale.setInstance(new MiniLocaleVelocity(proxyServer));
  }
}
