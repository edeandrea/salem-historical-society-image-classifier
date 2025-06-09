package org.salemhist.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.salemhist.domain.Artifact;
import org.salemhist.domain.NewArtifact;

class ArtifactMapperTests {
  ArtifactMapper mapper = Mappers.getMapper(ArtifactMapper.class);

  @Test
  void mappingWorks() {
    var newArtifact = new NewArtifact(
        "This is an image",
        "This is a reference",
        "https://www.google.com",
        "tools",
        "This is a description",
        "src/test/resources/images/1.jpg",
        "target/docs/1.docx",
        "https://drive.google.com/file/d/1234567890/view?usp=sharing"
    );

    var expectedArtifact = new Artifact();
    expectedArtifact.setImageDescription(newArtifact.imageDescription());
    expectedArtifact.setReferenceDescription(newArtifact.referenceDescription());
    expectedArtifact.setReferenceUrl(newArtifact.referenceUrl());
    expectedArtifact.setCategoryName(newArtifact.categoryName());
    expectedArtifact.setCategoryDescription(newArtifact.categoryDescription());
    expectedArtifact.setInputFile(newArtifact.inputFile());
    expectedArtifact.setOutputFile(newArtifact.outputFile());
    expectedArtifact.setGoogleDriveLink(newArtifact.googleDriveLink());

    assertThat(this.mapper.toArtifact(newArtifact))
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(expectedArtifact);
  }
}