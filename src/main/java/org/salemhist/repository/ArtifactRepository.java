package org.salemhist.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.salemhist.domain.Artifact;
import org.salemhist.domain.ArtifactDescription;
import org.salemhist.domain.Category;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<Artifact> {
  @Transactional
  public void saveArtifact(ArtifactDescription artifactDescription, Category category) {
    persist(artifactDescription.asArtifact(category));
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
