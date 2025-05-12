package org.salemhist.repository;

import jakarta.enterprise.context.ApplicationScoped;

import org.salemhist.domain.Artifact;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ArtifactRepository implements PanacheRepository<Artifact> {
}
