package io.salemhist.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileReader {
  private static final String ROOT_DIR = "src/main/resources/images";

  public String getBase64EncodedFile(String fileName) {
    try {
      var fileBytes = Files.readAllBytes(Path.of(ROOT_DIR, fileName));
      return Base64.getEncoder().encodeToString(fileBytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getMimeType(String fileName) {
    try {
      return Files.probeContentType(Path.of(ROOT_DIR, fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
