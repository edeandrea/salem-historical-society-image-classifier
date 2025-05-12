package org.salemhist.domain;

public record ErrorEvent(String message, Throwable throwable) {
  public ErrorEvent(Throwable throwable) {
    this(throwable.getMessage(), throwable);
  }
}
