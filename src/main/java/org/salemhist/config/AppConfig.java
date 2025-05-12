package org.salemhist.config;

import java.nio.file.Path;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app")
public interface AppConfig {
  @WithDefault("src/main/resources/images")
  Path rootImageDir();

  @WithDefault("target/docs")
  Path outputDir();

  @WithDefault("true")
  boolean cleanOutputDirAtStartup();
}
