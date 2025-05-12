package org.salemhist.domain;

public record Category(String name, String description) {
  public Category(String name) {
    this(name, null);
  }

  public boolean hasDescription() {
    return this.description != null;
  }
}
