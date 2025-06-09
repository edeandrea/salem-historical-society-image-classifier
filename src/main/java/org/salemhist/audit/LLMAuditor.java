package org.salemhist.audit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.quarkus.logging.Log;

import io.quarkiverse.langchain4j.audit.InitialMessagesCreatedEvent;
import io.quarkiverse.langchain4j.audit.InputGuardrailExecutedEvent;
import io.quarkiverse.langchain4j.audit.LLMInteractionCompleteEvent;
import io.quarkiverse.langchain4j.audit.LLMInteractionFailureEvent;
import io.quarkiverse.langchain4j.audit.OutputGuardrailExecutedEvent;
import io.quarkiverse.langchain4j.audit.ResponseFromLLMReceivedEvent;
import io.quarkiverse.langchain4j.audit.ToolExecutedEvent;

@ApplicationScoped
public class LLMAuditor {
  public void initialMessagesCreated(@Observes InitialMessagesCreatedEvent e) {
    Log.infof("[AUDIT] - Initial messages created: %s", e);
  }

  public void llmInteractionComplete(@Observes LLMInteractionCompleteEvent e) {
    Log.infof("[AUDIT] - LLM interaction complete: %s", e);
  }

  public void llmInteractionFailed(@Observes LLMInteractionFailureEvent e) {
    Log.errorf(e.error(), "[AUDIT] - LLM error encountered: %s", e);
  }

  public void responseFromLLMReceived(@Observes ResponseFromLLMReceivedEvent e) {
    Log.infof("[AUDIT] - LLM response received: %s", e);
  }

  public void toolExecuted(@Observes ToolExecutedEvent e) {
    Log.infof("[AUDIT] - Tool executed: %s", e);
  }

  public void inputGuardrailExecuted(@Observes InputGuardrailExecutedEvent e) {
    Log.infof("[AUDIT] - Input guardrail executed: %s", e);
  }

  public void outputGuardrailExecuted(@Observes OutputGuardrailExecutedEvent e) {
    Log.infof("[AUDIT] - Output guardrail executed: %s", e);
  }
}
