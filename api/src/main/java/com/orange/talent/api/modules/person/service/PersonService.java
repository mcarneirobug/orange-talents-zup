package com.orange.talent.api.modules.person.service;

import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;

public interface PersonService {

    PersonResponseDTO createPersonAndBet(PersonRequestDTO requestDTO);

    PersonResponseDTO findBetByEmail(String email) throws PersonNotFoundException;

}
