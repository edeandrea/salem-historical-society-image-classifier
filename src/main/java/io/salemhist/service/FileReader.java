package io.salemhist.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileReader {
  public String getBase64EncodedFile(String fileName) {
    try {
      var fileBytes = Files.readAllBytes(Path.of("src/main/resources/images", fileName));
      return Base64.getEncoder().encodeToString(fileBytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
