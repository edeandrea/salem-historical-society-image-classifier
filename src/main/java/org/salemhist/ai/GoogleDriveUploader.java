package org.salemhist.ai;

import java.nio.file.Path;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.domain.Artifact;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.guardrails.OutputGuardrails;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

@RegisterAiService
@ApplicationScoped
@Description("Understands how to interact with google drive")
@SystemMessage("""
      You are a service that knows how to interact with Google Drive. You have access to many tools.
      You can upload files, create shareable links, create folders, delete folders, and many other operations as described by the tools given to you.
      """)
public interface GoogleDriveUploader {
  @UserMessage("""
      Please perform the following operations:
      
      1) Create the folder "{gdriveRoot}/{artifact.categoryName}" (and any parent folders) if it does not exist.
      2) Upload "{fileToUpload}" to Google Drive as "{gdriveRoot}/{artifact.categoryName}/{artifact.getOutputFileName()}".
      3) Add a sharing permission of type "anyone" to the sharing preferences of the file and provide a sharing URL.
      """)
  @McpToolBox("google-drive")
  @Tool("Uploads a document to Google Drive and adds a sharing permission")
  @OutputGuardrails(GoogleDriveUploadResultOutputJsonGuardrail.class)
  GoogleDriveUploadResult uploadToGoogleDriveAndAddSharingPermission(@ToolMemoryId @MemoryId Object memoryId, String gdriveRoot, Artifact artifact, Path fileToUpload);

  record GoogleDriveUploadResult(@Description("The sharing URL for the file") String sharingURL) {}
}
