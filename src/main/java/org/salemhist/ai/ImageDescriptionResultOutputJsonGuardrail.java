package org.salemhist.ai;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.ai.ImageDescriber.ImageDescriptionResult;

import io.quarkiverse.langchain4j.guardrails.AbstractJsonExtractorOutputGuardrail;

@ApplicationScoped
public class ImageDescriptionResultOutputJsonGuardrail extends AbstractJsonExtractorOutputGuardrail {
  @Override
  protected Class<?> getOutputClass() {
    return ImageDescriptionResult.class;
  }
}
