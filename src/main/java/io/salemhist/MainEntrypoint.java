package io.salemhist;

import dev.langchain4j.data.image.Image;
import io.salemhist.ai.ImageDescriber;
import io.salemhist.service.FileReader;
import picocli.CommandLine.Command;

@Command(name = "describeImage")
public class MainEntrypoint implements Runnable {
  private final ImageDescriber imageDescriber;
  private final FileReader fileReader;

  public MainEntrypoint(ImageDescriber imageDescriber, FileReader fileReader) {
    this.imageDescriber = imageDescriber;
    this.fileReader = fileReader;
  }

  @Override
  public void run() {
    var image = Image.builder()
        .base64Data(this.fileReader.getBase64EncodedFile("image.png"))
        .mimeType(this.fileReader.getMimeType("image.png"))
        .build();

    System.out.println(this.imageDescriber.describeImage(image, "tool"));
  }
}
