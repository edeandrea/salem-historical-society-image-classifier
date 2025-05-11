package io.salemhist.ai.guardrail;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.langchain4j.guardrails.AbstractJsonExtractorOutputGuardrail;
import io.salemhist.domain.ImageDescription;

@ApplicationScoped
public class ImageDescriptionOutputJsonGuardrail extends AbstractJsonExtractorOutputGuardrail {
  @Override
  protected Class<?> getOutputClass() {
    return ImageDescription.class;
  }
}
