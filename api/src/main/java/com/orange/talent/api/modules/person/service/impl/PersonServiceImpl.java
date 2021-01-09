package com.orange.talent.api.modules.person.service.impl;

import com.orange.talent.api.model.Person;
import com.orange.talent.api.modules.person.dto.PersonDTO;
import com.orange.talent.api.modules.person.exception.PersonAlreadyRegisteredException;
import com.orange.talent.api.modules.person.mapper.PersonMapper;
import com.orange.talent.api.modules.person.repository.PersonRepository;
import com.orange.talent.api.modules.person.service.PersonService;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonDTO createPerson(PersonDTO personDTO) throws PersonAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(personDTO.getEmail());
        final var person = personMapper.toModel(personDTO);
        final var saved = this.personRepository.save(person);

        return personMapper.toDTO(saved);
    }

    @Override
    public PersonDTO findByEmail(String email) throws NotFoundException {
        return this.personRepository.findByEmail(email)
                .map(personMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(""));
    }

    private void verifyIfIsAlreadyRegistered(String email) throws PersonAlreadyRegisteredException {
        Optional<Person> optSavedBeer = personRepository.findByEmail(email);
        if (optSavedBeer.isPresent()) {
            throw new PersonAlreadyRegisteredException(email);
        }
    }


}
