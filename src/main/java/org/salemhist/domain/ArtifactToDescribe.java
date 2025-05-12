package org.salemhist.domain;

import java.nio.file.Path;

public record ArtifactToDescribe(Path imageFile, Category category) {
  public boolean hasCategoryDescription() {
    return category().hasDescription();
  }
}
