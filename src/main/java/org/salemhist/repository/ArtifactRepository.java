package org.salemhist.repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.salemhist.config.AppConfig;
import org.salemhist.domain.Artifact;
import org.salemhist.domain.Category;
import org.salemhist.domain.NewArtifact;
import org.salemhist.mapping.ArtifactMapper;
import org.salemhist.service.FileReader;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<Artifact> {
  private final AppConfig appConfig;
  private final ArtifactMapper artifactMapper;

  public ArtifactRepository(AppConfig appConfig, ArtifactMapper artifactMapper) {
    this.appConfig = appConfig;
    this.artifactMapper = artifactMapper;
  }

  @Transactional
  @Tool("Saves an artifact to the database")
  public String saveArtifact(NewArtifact newArtifact) {
    var inputFile = Path.of(newArtifact.inputFile()).toAbsolutePath();
    var outputFile = Path.of(
        Optional.ofNullable(newArtifact.outputFile())
            .orElseGet(() -> "%s/%s".formatted(this.appConfig.outputDir(), FileReader.OUTPUT_FILE_NAME_TEMPLATE.formatted(inputFile.getFileName().toString())))
    ).toAbsolutePath();

    var artifact = this.artifactMapper.toArtifact(newArtifact);
    artifact.setInputFile(this.appConfig.rootImageDir().toAbsolutePath().relativize(inputFile).toString());
    artifact.setOutputFile(this.appConfig.outputDir().toAbsolutePath().relativize(outputFile).toString());
    persist(artifact);

    return "Artifact successfully saved to the database";
  }

  public List<Artifact> getAllSortedByCategory() {
    return listAll(Sort.by("categoryName"));
  }

  public List<Artifact> getAllByCategory(Category category) {
    return getAllByCategoryName(category.name());
  }

  public List<Artifact> getAllByCategoryName(String categoryName) {
    return list("categoryName", categoryName);
  }
}
