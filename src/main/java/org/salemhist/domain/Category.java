package org.salemhist.domain;

import dev.langchain4j.model.output.structured.Description;

public record Category(@Description("The category name") String name, @Description("The category description") String description) {
  public Category(String name) {
    this(name, null);
  }

  public boolean hasDescription() {
    return this.description != null;
  }
}
