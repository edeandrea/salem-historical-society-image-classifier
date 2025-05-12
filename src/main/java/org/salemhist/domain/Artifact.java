package org.salemhist.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "artifacts")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "artifacts_seq", sequenceName = "artifacts_seq", allocationSize = 1)
    private Long id;

    @Column
    private String imageDescription;

    @Column
    private String referenceDescription;

    @Column
    private String referenceURL;

    public static Artifact from(ArtifactDescription artifactDescription) {
        var artifact = new Artifact();
        artifact.setImageDescription(artifactDescription.imageDescription());
        artifact.setReferenceDescription(artifactDescription.referenceDescription());
        artifact.setReferenceURL(artifactDescription.referenceURL().toString());

        return artifact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = referenceDescription;
    }

    public String getReferenceURL() {
        return referenceURL;
    }

    public void setReferenceURL(String referenceURL) {
        this.referenceURL = referenceURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact that = (Artifact) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "Artifact{" +
               "id=" + id +
               ", imageDescription='" + imageDescription + '\'' +
               ", referenceDescription='" + referenceDescription + '\'' +
               ", referenceURL='" + referenceURL + '\'' +
               '}';
    }
}
