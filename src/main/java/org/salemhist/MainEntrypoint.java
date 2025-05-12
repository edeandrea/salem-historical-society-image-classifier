package org.salemhist;

import java.io.IOException;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;

import org.salemhist.ai.ImageDescriber;
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

  public MainEntrypoint(ImageDescriber imageDescriber, FileReader fileReader, ArtifactRepository artifactRepository, Event<ErrorEvent> errorEventPublisher) {
    this.imageDescriber = imageDescriber;
    this.fileReader = fileReader;
    this.artifactRepository = artifactRepository;
    this.errorEventPublisher = errorEventPublisher;
  }

  @Override
  @ActivateRequestContext
  public int run(String... args) {
    loadImageFiles();
    printArtifacts();

    return 0;
  }

  private void printArtifacts() {
    if (Log.isDebugEnabled()) {
      System.out.println("======== IMAGES TO PROCESS ==========");
      this.artifactRepository.listAll().forEach(this::printArtifact);
    }
  }

  private void printArtifact(Artifact artifact) {
    System.out.println("-----------------------------------");
    System.out.println("IMAGE DESCRIPTION:     %s".formatted(artifact.getImageDescription()));
    System.out.println("REFERENCE DESCRIPTION: %s".formatted(artifact.getReferenceDescription()));
    System.out.println("REFERENCE URL:         %s".formatted(artifact.getReferenceUrl()));
    System.out.println("CATEGORY NAME:         %s".formatted(artifact.getCategoryName()));
    System.out.println("CATEGORY DESCRIPTION:  %s".formatted(artifact.getCategoryDescription()));
    System.out.println("-----------------------------------");
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

  private void processImageFile(ArtifactToDescribe artifactToDescribe) {
    this.fileReader.getImage(artifactToDescribe.imageFile())
        .map(image -> artifactToDescribe.hasCategoryDescription() ? this.imageDescriber.describeImage(image, artifactToDescribe.category()) : this.imageDescriber.describeImage(image, artifactToDescribe.category().name()))
        .ifPresentOrElse(
            artifactDescription -> this.artifactRepository.saveArtifact(artifactDescription, artifactToDescribe.category()),
            () -> System.out.println("Couldn't find image file")
        );
  }
}
