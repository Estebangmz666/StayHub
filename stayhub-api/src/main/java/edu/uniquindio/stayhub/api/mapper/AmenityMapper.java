package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.accommodation.AmenityDTO;
import edu.uniquindio.stayhub.api.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AmenityMapper {
    AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    Amenity toEntity(AmenityDTO dto);

    AmenityDTO toDto(Amenity amenity);
}