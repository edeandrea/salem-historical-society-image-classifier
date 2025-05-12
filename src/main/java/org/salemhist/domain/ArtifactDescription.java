package org.salemhist.domain;

import java.net.URL;

import dev.langchain4j.model.output.structured.Description;

public record ArtifactDescription(@Description("Description of the image") String imageDescription,
                                  @Description("Text to describe the URL to the reference") String referenceDescription,
                                  @Description("URL to where more information about the image can be found") URL referenceURL) {

  public ArtifactDescription(String imageDescription, String referenceDescription) {
    this(imageDescription, referenceDescription, null);
  }

  public Artifact asArtifact(Category category) {
    var artifact = new Artifact();
    artifact.setImageDescription(this.imageDescription());
    artifact.setReferenceDescription(this.referenceDescription());
    artifact.setReferenceUrl(this.referenceURL().toString());
    artifact.setCategoryName(category.name());
    artifact.setCategoryDescription(category.description());

    return artifact;
  }
}
