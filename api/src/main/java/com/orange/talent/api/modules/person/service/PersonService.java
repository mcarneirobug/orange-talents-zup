package com.orange.talent.api.modules.person.service;

import com.orange.talent.api.modules.person.dto.PersonDTO;
import com.orange.talent.api.modules.person.exception.PersonAlreadyRegisteredException;
import javassist.NotFoundException;

import java.util.Optional;

public interface PersonService {

    PersonDTO createPerson(PersonDTO personDTO) throws PersonAlreadyRegisteredException;

    PersonDTO findByEmail(String email) throws NotFoundException;

}
