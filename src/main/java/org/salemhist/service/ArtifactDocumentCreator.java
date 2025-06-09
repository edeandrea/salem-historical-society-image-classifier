package org.salemhist.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.salemhist.domain.Artifact;
import org.salemhist.domain.ErrorEvent;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class ArtifactDocumentCreator {
  private static final Set<PictureType> SUPPORTED_IMAGE_TYPES =
      EnumSet.complementOf(EnumSet.of(PictureType.CLIENT, PictureType.CMYKJPEG, PictureType.ERROR, PictureType.UNKNOWN));

  private final Event<ErrorEvent> errorEventPublisher;

  public ArtifactDocumentCreator(Event<ErrorEvent> errorEventPublisher) {
    this.errorEventPublisher = errorEventPublisher;
  }

  @Tool("Creates a Microsoft Word .docx document from the given artifact and saves it to the requested location")
  public String createAndSaveDocument(Artifact artifact, String imageSource, String outputFile) {
    try {
      var document = new XWPFDocument();
      // Add title
//      var titleParagraph = document.createParagraph();
//      var titleRun = titleParagraph.createRun();
//      titleRun.setBold(true);
//      titleRun.setFontSize(16);
//      titleRun.setText("Artifact Documentation");
//      titleRun.addBreak();

      // Add image
      addImage(document, imageSource);

      // Add image description
      addImageDescription(document, artifact);

      // Add category information
      addCategory(document, artifact);

      // Add reference information and URL if available
      addReferenceInfo(document, artifact);

      // Save the document to the specified location
      saveDocument(document, outputFile);
    }
    catch (IOException e) {
      // Handle exceptions appropriately
      this.errorEventPublisher.fire(new ErrorEvent("Error creating word document for image %s".formatted(imageSource), e));
      return "Error creating word document for image %s: %s".formatted(imageSource, e.getMessage());
    }

    return "Document successfully created and saved as %s".formatted(outputFile);
  }

  private void saveDocument(XWPFDocument document, String outputFile) throws IOException {
    try (var outputStream = Files.newOutputStream(Path.of(outputFile))) {
      document.write(outputStream);
    }
  }

  private void addReferenceInfo(XWPFDocument document, Artifact artifact) {
    if ((artifact.getReferenceDescription()) != null && !artifact.getReferenceDescription().isEmpty()) {
      var referenceParagraph = document.createParagraph();
      var referenceTitleRun = referenceParagraph.createRun();
      referenceTitleRun.setBold(true);
      referenceTitleRun.setText("Reference:");
      referenceTitleRun.addBreak();

      var referenceRun = referenceParagraph.createRun();
      referenceRun.setText(artifact.getReferenceDescription());
      referenceRun.addBreak();
    }

    // Add hyperlink if URL is available
    if ((artifact.getReferenceUrl() != null) && !artifact.getReferenceUrl().isEmpty()) {
      var urlParagraph = document.createParagraph();
      var urlTitleRun = urlParagraph.createRun();
      urlTitleRun.setBold(true);
      urlTitleRun.setText("Reference URL: ");

      // Create hyperlink
      var url = artifact.getReferenceUrl();

      // Add a run with the URL text
      var urlRun = urlParagraph.createRun();
      urlRun.setText(url);
      urlRun.setColor("0000FF");
      urlRun.setUnderline(UnderlinePatterns.SINGLE);

      // Add the hyperlink using the document's package relationships
      // Use the direct string constant for the relationship type
      String rId = document.getPackagePart()
          .addExternalRelationship(url, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink").getId();

      // Add the relationship to the run's parent paragraph
      urlParagraph.getCTP().addNewHyperlink().setId(rId);
    }
  }

  private void addCategory(XWPFDocument document, Artifact artifact) {
    var categoryParagraph = document.createParagraph();
    var categoryTitleRun = categoryParagraph.createRun();
    categoryTitleRun.setBold(true);
    categoryTitleRun.setText("Category: %s".formatted(artifact.getCategoryName()));
    categoryTitleRun.addBreak();

    var categoryRun = categoryParagraph.createRun();

    if ((artifact.getCategoryDescription() != null) && !artifact.getCategoryDescription().isEmpty()) {
      categoryRun.setText(artifact.getCategoryDescription());
    }

    categoryRun.addBreak();
  }

  private void addImageDescription(XWPFDocument document, Artifact artifact) {
    var descriptionParagraph = document.createParagraph();
    var descriptionTitleRun = descriptionParagraph.createRun();
    descriptionTitleRun.setBold(true);
    descriptionTitleRun.setText("Description:");
    descriptionTitleRun.addBreak();

    var descriptionRun = descriptionParagraph.createRun();
    descriptionRun.setText(artifact.getImageDescription());
    descriptionRun.addBreak();
  }

  private void addImage(XWPFDocument document, String imageSource) throws IOException {
    var imageSourcePath = Path.of(imageSource);

    getPictureType(imageSourcePath)
        .ifPresent(pictureType -> {
          try (var imageStream = Files.newInputStream(imageSourcePath)) {
            var image = ImageIO.read(imageSourcePath.toFile());

            document.createParagraph()
                .createRun()
                .addPicture(
                    imageStream,
                    pictureType,
                    imageSourcePath.getFileName().toString(),
                    Units.toEMU(image.getWidth()),
                    Units.toEMU(image.getHeight())
                );
          }
          catch (IOException | InvalidFormatException e) {
            this.errorEventPublisher.fire(new ErrorEvent("Error adding image file %s to word document".formatted(imageSource), e));
          }
        });
  }

  private Optional<PictureType> getPictureType(Path imageSource) {
    return SUPPORTED_IMAGE_TYPES.stream()
        .filter(imageType -> imageSource.getFileName().toString().endsWith(imageType.getExtension()))
        .findFirst();
  }
}