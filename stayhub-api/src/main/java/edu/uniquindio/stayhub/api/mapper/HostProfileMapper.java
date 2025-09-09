package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.host.HostProfileDTO;
import edu.uniquindio.stayhub.api.model.HostProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostProfileMapper {
    HostProfileMapper INSTANCE = Mappers.getMapper(HostProfileMapper.class);

    @Mapping(target = "user", ignore = true)
    HostProfile toEntity(HostProfileDTO dto);

    HostProfileDTO toDto(HostProfile hostProfile);
}