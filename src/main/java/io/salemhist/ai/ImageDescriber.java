package io.salemhist.ai;

import jakarta.enterprise.context.ApplicationScoped;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ImageUrl;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.guardrails.OutputGuardrails;
import io.salemhist.ai.guardrail.ImageDescriptionOutputJsonGuardrail;
import io.salemhist.domain.ImageDescription;

@RegisterAiService
@ApplicationScoped
public interface ImageDescriber {
  @SystemMessage("""
      You are a service which helps identify historical things. You should provide a 2-3 sentence description of the image sent to you. The description should be simple enough for an 11 year old child to understand.
      
      Please include the era or time period in the description.
      
      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.
      
      DO NOT use markdown (or any other formatting language) in the response.
      """)
//  @SystemMessage("""
//      You are a service which helps identify historical things. You should provide a 2-3 sentence description of the image sent to you. The description should be simple enough for an 11 year old child to understand.
//
//      Please include the era or time period in the description.
//
//      Additionally, please find a reference to this picture on a website (like Wikipedia for example) and return a URL to it. Please include the URL as text in a separate paragraph.
//
//      DO NOT use markdown (or any other formatting language) in the response.
//
//      Please only generate the response as a Microsoft Word document (.docx) format. The document should contain the original image and the response you've generated.
//      """)
  @UserMessage("This image is some kind of {category}.")
  @OutputGuardrails(ImageDescriptionOutputJsonGuardrail.class)
  ImageDescription describeImage(@ImageUrl Image image, String category);
}
