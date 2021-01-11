package com.orange.talent.api.modules.person.mapper;

import com.orange.talent.api.model.Person;
import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toModel(PersonResponseDTO responseDTO);

    PersonResponseDTO toDTO(Person person);

    Person to(PersonRequestDTO requestDTO);
}
