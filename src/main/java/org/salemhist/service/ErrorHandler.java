package org.salemhist.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.salemhist.domain.ErrorEvent;

import io.quarkus.logging.Log;

@ApplicationScoped
public class ErrorHandler {
  public void handleError(@Observes ErrorEvent errorEvent) {
    Log.errorf(errorEvent.throwable(), "[PROCESSING ERROR] - %s", errorEvent.message());
  }
}
