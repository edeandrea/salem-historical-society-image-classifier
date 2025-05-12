package org.salemhist.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

import org.salemhist.config.AppConfig;
import org.salemhist.domain.ArtifactToDescribe;
import org.salemhist.domain.Category;
import org.salemhist.domain.ErrorEvent;

import io.quarkus.logging.Log;

import dev.langchain4j.data.image.Image;

@ApplicationScoped
public class FileReader {
  private static final String CATEGORY_DESCRIPTION_FILENAME = "description.txt";
  private final AppConfig appConfig;
  private final Event<ErrorEvent> errorEventPublisher;

  public FileReader(AppConfig appConfig, Event<ErrorEvent> errorEventPublisher) {
    this.appConfig = appConfig;
    this.errorEventPublisher = errorEventPublisher;
  }

  public Stream<ArtifactToDescribe> getAllImagesInSubdirectories() throws IOException {
    var categoryDescriptions = getCategoryDescriptions();

    return Files.list(this.appConfig.rootImageDir())
        .filter(Files::isDirectory)
        .flatMap(categoryDir -> {
          var categoryName = categoryDir.getFileName().toString();

          try {
            return Files.list(categoryDir)
                .filter(file -> !CATEGORY_DESCRIPTION_FILENAME.equals(file.getFileName().toString()) && !Files.isDirectory(file) && isImageFile(file))
                .map(imageFile -> new ArtifactToDescribe(imageFile, categoryDescriptions.getOrDefault(categoryName, new Category(categoryName))));
          }
          catch (IOException e) {
            this.errorEventPublisher.fire(new ErrorEvent("Error reading category directory %s".formatted(categoryDir), e));
            return Stream.empty();
          }
        });
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

  private Map<String, Category> getCategoryDescriptions() throws IOException {
    return Files.list(this.appConfig.rootImageDir())
        .filter(Files::isDirectory)
        .flatMap(dir -> {
              try {
                var categoryName = dir.getFileName().toString();
                Log.infof("Processing category \"%s\" from directory %s", categoryName, dir);

                return Files.list(dir)
                    .filter(file -> CATEGORY_DESCRIPTION_FILENAME.equals(file.getFileName().toString()) && !Files.isDirectory(file))
                    .map(file -> {
                      try {
                        return new Category(categoryName, Files.readString(file));
                      }
                      catch (IOException e) {
                        this.errorEventPublisher.fire(new ErrorEvent("Error reading category file %s".formatted(file), e));
                        return new Category(categoryName);
                      }
                    });
              }
              catch (IOException e) {
                this.errorEventPublisher.fire(new ErrorEvent("Error reading category directory %s".formatted(dir), e));
                return null;
              }
            })
        .collect(Collectors.toMap(Category::name, Function.identity()));
  }
}
