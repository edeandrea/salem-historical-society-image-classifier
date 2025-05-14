package org.salemhist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;

import org.salemhist.ai.ImageDescriber;
import org.salemhist.ai.PerFileChatMemoryProvider;
import org.salemhist.config.AppConfig;
import org.salemhist.domain.Artifact;
import org.salemhist.domain.ArtifactToDescribe;
import org.salemhist.domain.ErrorEvent;
import org.salemhist.repository.ArtifactRepository;
import org.salemhist.service.FileReader;

import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class MainEntrypoint implements QuarkusApplication {
  private final ImageDescriber imageDescriber;
  private final FileReader fileReader;
  private final ArtifactRepository artifactRepository;
  private final Event<ErrorEvent> errorEventPublisher;
  private final AppConfig appConfig;
  private final PerFileChatMemoryProvider chatMemoryProvider;

  public MainEntrypoint(ImageDescriber imageDescriber, FileReader fileReader, ArtifactRepository artifactRepository, Event<ErrorEvent> errorEventPublisher, AppConfig appConfig, PerFileChatMemoryProvider chatMemoryProvider) {
    this.imageDescriber = imageDescriber;
    this.fileReader = fileReader;
    this.artifactRepository = artifactRepository;
    this.errorEventPublisher = errorEventPublisher;
    this.appConfig = appConfig;
    this.chatMemoryProvider = chatMemoryProvider;
  }

  @Override
  @ActivateRequestContext
  public int run(String... args) {
    cleanupOutputDirectory();
    loadImageFiles();
    printArtifacts();

    return 0;
  }

  private void cleanupOutputDirectory() {
    if (this.appConfig.cleanOutputDirAtStartup() && Files.exists(this.appConfig.outputDir())) {
      try {
        Files.walk(this.appConfig.outputDir())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void printArtifacts() {
    if (Log.isDebugEnabled()) {
      Log.debug("======== IMAGES TO PROCESS ==========");
      this.artifactRepository.listAll().forEach(this::printArtifact);
    }
  }

  private void printArtifact(Artifact artifact) {
    Log.debug("-----------------------------------");
    Log.debugf("IMAGE DESCRIPTION:     %s", artifact.getImageDescription());
    Log.debugf("REFERENCE DESCRIPTION: %s", artifact.getReferenceDescription());
    Log.debugf("REFERENCE URL:         %s", artifact.getReferenceUrl());
    Log.debugf("CATEGORY NAME:         %s", artifact.getCategoryName());
    Log.debugf("CATEGORY DESCRIPTION:  %s", artifact.getCategoryDescription());
    Log.debugf("RELATIVE INPUT PATH:   %s", artifact.getInputFile());
    Log.debugf("RELATIVE OUTPUT PATH:  %s", artifact.getOutputFile());
    Log.debugf("INPUT FILE:            %s", this.appConfig.rootImageDir().resolve(artifact.getInputFile()).toAbsolutePath());
    Log.debugf("OUTPUT FILE:           %s", this.appConfig.outputDir().resolve(artifact.getOutputFile()).toAbsolutePath());
    Log.debug("-----------------------------------");
  }

  private void loadImageFiles() {
    try {
      this.fileReader.getAllImagesInSubdirectories()
          .forEach(this::processImageFile);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createOutputDirIfNotExists(ArtifactToDescribe artifactToDescribe) {
    var parentDir = this.appConfig.outputDir().resolve(artifactToDescribe.category().name()).toAbsolutePath();

    if (Files.notExists(parentDir)) {
      Log.debugf("Creating directory %s", parentDir);

      try {
        Files.createDirectories(parentDir);
      }
      catch (IOException e) {
        this.errorEventPublisher.fire(new ErrorEvent("Error creating directory %s".formatted(parentDir), e));
      }
    }
  }

  private void processImageFile(ArtifactToDescribe artifactToDescribe) {
    createOutputDirIfNotExists(artifactToDescribe);

    var outputFile = this.appConfig.outputDir()
        .resolve(artifactToDescribe.category().name())
        .resolve(FileReader.OUTPUT_FILE_NAME_TEMPLATE.formatted(artifactToDescribe.imageFile().getFileName().toString()))
        .toAbsolutePath();

    Log.debugf("Output file location: %s", outputFile);
    var memoryId = UUID.randomUUID().toString();

    this.fileReader.getImage(artifactToDescribe.imageFile())
        .map(image ->
            artifactToDescribe.hasCategoryDescription() ?
                this.imageDescriber.describeImage(memoryId, image, artifactToDescribe, artifactToDescribe.category().description(), outputFile) :
                this.imageDescriber.describeImage(memoryId, image, artifactToDescribe, outputFile)
        )
        .ifPresentOrElse(
            Log::debug,
            () -> Log.info("Couldn't find image file")
        );
  }
}
