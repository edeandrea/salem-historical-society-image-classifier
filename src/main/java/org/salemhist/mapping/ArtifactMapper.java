package org.salemhist.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.salemhist.domain.Artifact;
import org.salemhist.domain.NewArtifact;

@Mapper(componentModel = ComponentModel.JAKARTA_CDI)
public interface ArtifactMapper {
  Artifact toArtifact(NewArtifact newArtifact);
}
