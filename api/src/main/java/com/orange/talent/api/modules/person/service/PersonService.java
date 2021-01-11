package com.orange.talent.api.modules.person.service;

import com.orange.talent.api.modules.person.dto.PersonDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;

public interface PersonService {

    PersonDTO createPersonAndBet(PersonDTO personDTO);

    PersonDTO findBetByEmail(String email) throws PersonNotFoundException;

}
