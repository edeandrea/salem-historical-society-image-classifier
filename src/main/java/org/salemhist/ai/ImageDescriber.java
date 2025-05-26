package org.salemhist.ai;

import java.nio.file.Path;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.ai.GoogleDriveUploader.GoogleDriveUploadResult;
import org.salemhist.domain.ArtifactToDescribe;
import org.salemhist.repository.ArtifactRepository;
import org.salemhist.service.ArtifactDocumentCreator;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ImageUrl;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.guardrails.OutputGuardrails;

@RegisterAiService
@ApplicationScoped
//@OutputGuardrails(ImageDescriptionResultOutputJsonGuardrail.class)
//@SystemMessage("""
//      You are a service which helps identify historical objects from a picture. You should a description (4 sentences maximum) of an image.
//      The description should be simple enough for an 11 year old child to understand. Please include the era or time period in the description.
//
//      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.
//
//      DO NOT use markdown (or any other format) in the response.
//
//      Once you have that information, please perform the following operations:
//      1) Save the artifact details into the database
//      2) Create a Microsoft Word docx document containing the original image embedded in the document, the returned image description, and if applicable, the reference URL. Please ensure the reference URL is a clickable hyperlink.
//
//      The image's input file is {artifactToDescribe.imageFile}.
//      The document's output file is {fileToSave}.
//      """)
//@SystemMessage("""
//      You are a service which helps identify historical objects from a picture. You should provide a 2-3 sentence description of the image sent to you. The description should be simple enough for an 11 year old child to understand.
//
//      Please include the era or time period in the description.
//
//      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.
//
//      DO NOT use markdown (or any other format) in the response.
//
//      Once you have that information, please perform the following operations:
//      1) Save the artifact details into the database
//      2) Create a Microsoft Word docx document containing the original image embedded in the document, the returned image description, and if applicable, the reference URL. Please ensure the reference URL is a clickable hyperlink.
//      3) Save the file as "{fileToSave}"
//      """)
@SystemMessage("""
      You are a service which helps identify historical objects from a picture. You should a description (4 sentences maximum) of an image.
      The description should be simple enough for an 11 year old child to understand. Please include the era or time period in the description.

      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.

      Once you have that information, create a Microsoft Word docx document located at {fileToSave} with the following information:
      - The original image embedded in the document
      - The returned image description
      - If applicable, the reference URL. Please ensure the reference URL is a clickable hyperlink.

      Once the Microsoft Word document has been created, perform the following steps. Please don't ask for confirmation before performing any of the steps. Just do it.
      1) Upload the Microsoft Word docx document "{fileToSave}" to Google Drive inside the root folder "{gdriveRoot}".
      2) Add a sharing permission granting world read access.
      3) Save the artifact details into the database

      The image's input file is {artifactToDescribe.imageFile}.
      The document's output file is {fileToSave}.
      """)
//@SystemMessage("""
//      You are a service which helps identify historical objects from a picture. You should a description (4 sentences maximum) of an image.
//      The description should be simple enough for an 11 year old child to understand. Please include the era or time period in the description.
//
//      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.
//
//      Once you have that information, create a Microsoft Word docx document located at {fileToSave} with the following information:
//      - The original image embedded in the document
//      - The returned image description
//      - If applicable, the reference URL. Please ensure the reference URL is a clickable hyperlink.
//
//      The image's input file is {artifactToDescribe.imageFile}.
//      The document's output file is {fileToSave}.
//
//      DO NOT do anything with Google drive yet! Just write the file.
//      """)
public interface ImageDescriber {
  @UserMessage("This image is categorized as \"{artifactToDescribe.category.name}\", which is described as \"{categoryDescription}\".")
  @ToolBox({ ArtifactRepository.class, ArtifactDocumentCreator.class, GoogleDriveUploader.class })
  @OutputGuardrails(ImageDescriptionResultOutputJsonGuardrail.class)
  ImageDescriptionResult describeImage(@MemoryId Object memoryId, @ImageUrl Image image, ArtifactToDescribe artifactToDescribe, String categoryDescription, Path fileToSave, String gdriveRoot);

  @UserMessage("This image is categorized as \"{artifactToDescribe.category.name}\"")
  @ToolBox({ ArtifactRepository.class, ArtifactDocumentCreator.class, GoogleDriveUploader.class })
  @OutputGuardrails(ImageDescriptionResultOutputJsonGuardrail.class)
  ImageDescriptionResult describeImage(@MemoryId Object memoryId, @ImageUrl Image image, ArtifactToDescribe artifact, Path fileToSave, String gdriveRoot);

  record ImageDescriptionResult(GoogleDriveUploadResult gdriveUploadResult) {}
}
