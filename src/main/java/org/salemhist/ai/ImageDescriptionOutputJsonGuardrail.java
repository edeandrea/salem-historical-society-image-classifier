package org.salemhist.ai;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.domain.Artifact;

import io.quarkiverse.langchain4j.guardrails.AbstractJsonExtractorOutputGuardrail;

@ApplicationScoped
public class ImageDescriptionOutputJsonGuardrail extends AbstractJsonExtractorOutputGuardrail {
  @Override
  protected Class<?> getOutputClass() {
    return Artifact.class;
  }
}
