package org.salemhist.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.quarkus.logging.Log;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import io.quarkiverse.langchain4j.audit.LLMInteractionCompleteEvent;

@ApplicationScoped
public class PerFileChatMemoryProvider implements ChatMemoryProvider {
  private final Map<Object, ChatMemory> memoryByMemoryId = new ConcurrentHashMap<>();

  @Override
  public ChatMemory get(Object memoryId) {
    return this.memoryByMemoryId.computeIfAbsent(memoryId, InMemoryChatMemory::new);
  }

  public void llmInteractionComplete(@Observes LLMInteractionCompleteEvent event) {
    event.sourceInfo().memoryIDParamPosition()
        .map(memoryParamPosition -> event.sourceInfo().methodParams()[memoryParamPosition])
        .ifPresent(this::remove);
  }

  public void remove(Object memoryId) {
    Log.debugf("Removing memory with id %s from memory provider", memoryId);
    Optional.ofNullable(this.memoryByMemoryId.remove(memoryId))
        .ifPresent(ChatMemory::clear);
  }

  private static class InMemoryChatMemory implements ChatMemory {
    private final List<ChatMessage> messages = new ArrayList<>();
    private final Object id;

    private InMemoryChatMemory(Object id) {
      this.id = id;
    }

    @Override
    public Object id() {
      return this.id;
    }

    @Override
    public void add(ChatMessage message) {
      this.messages.add(message);
    }

    @Override
    public List<ChatMessage> messages() {
      return this.messages;
    }

    @Override
    public void clear() {
      this.messages.clear();
    }
  }
}
