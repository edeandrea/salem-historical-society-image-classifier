package org.salemhist;

import java.io.IOException;
import java.nio.file.Path;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;

import org.salemhist.ai.ImageDescriber;
import org.salemhist.domain.ErrorEvent;
import org.salemhist.repository.ArtifactRepository;
import org.salemhist.service.FileReader;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
@ActivateRequestContext
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
  @Transactional
  public int run(String... args) {
    try {
      this.fileReader.getAllImagesInAllSubdirectories()
          .forEach(this::processImageFile);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    return 0;
  }

  private void processImageFile(Path file) {
    this.fileReader.getImage(file)
        .map(image -> this.imageDescriber.describeImage(image, "tool").asArtifact())
        .ifPresentOrElse(
            this.artifactRepository::persist,
            () -> System.out.println("Couldn't find image file")
        );
  }
}
