package org.salemhist.ai;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.ai.GoogleDriveUploader.GoogleDriveUploadResult;

import io.quarkiverse.langchain4j.guardrails.AbstractJsonExtractorOutputGuardrail;

@ApplicationScoped
public class GoogleDriveUploadResultOutputJsonGuardrail extends AbstractJsonExtractorOutputGuardrail {
  @Override
  protected Class<?> getOutputClass() {
    return GoogleDriveUploadResult.class;
  }
}
