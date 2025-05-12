package org.salemhist.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

import org.salemhist.config.AppConfig;
import org.salemhist.domain.ErrorEvent;

import dev.langchain4j.data.image.Image;

@ApplicationScoped
public class FileReader {
  private final AppConfig appConfig;
  private final Event<ErrorEvent> errorEventPublisher;

  public FileReader(AppConfig appConfig, Event<ErrorEvent> errorEventPublisher) {
    this.appConfig = appConfig;
    this.errorEventPublisher = errorEventPublisher;
  }

  public Stream<Path> getAllImagesInAllSubdirectories() throws IOException {
    return Files.walk(this.appConfig.rootImageDir())
        .filter(this::isImageFile);
  }

  public Optional<Image> getImage(Path file) {
    return getMimeType(file)
        .flatMap(mimeType ->
            getBase64EncodedFile(file)
                .map(base64Data -> Image.builder()
                    .base64Data(base64Data)
                    .mimeType(mimeType)
                    .build()
                )
        );
  }

  private Optional<String> getBase64EncodedFile(Path file) {
    try {
      var fileBytes = Files.readAllBytes(file);
      return Optional.ofNullable(Base64.getEncoder().encodeToString(fileBytes));
    }
    catch (IOException e) {
      this.errorEventPublisher.fire(new ErrorEvent("Error reading file %s".formatted(file), e));
    }

    return Optional.empty();
  }

  private Optional<String> getMimeType(Path file) {
    try {
      return Optional.ofNullable(Files.probeContentType(file));
    }
    catch (IOException e) {
      this.errorEventPublisher.fire(new ErrorEvent("Error getting mime type from file %s".formatted(file), e));
    }

    return Optional.empty();
  }

  private boolean isImageFile(Path file) {
    if (!Files.isDirectory(file)) {
      return getMimeType(file)
          .filter(f -> f.startsWith("image/"))
          .isPresent();
    }

    return false;
  }
}
