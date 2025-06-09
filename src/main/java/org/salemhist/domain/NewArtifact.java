package org.salemhist.domain;

import dev.langchain4j.model.output.structured.Description;

public record NewArtifact(
    @Description("The image description") String imageDescription,
    @Description("The reference description") String referenceDescription,
    @Description("The URL to the reference") String referenceUrl,
    @Description("The name of the category") String categoryName,
    @Description("The description of the category") String categoryDescription,
    @Description("The path to the source image file") String inputFile,
    @Description("The path to the output document") String outputFile,
    @Description("The link on Google Drive for the output document") String googleDriveLink) {
}
