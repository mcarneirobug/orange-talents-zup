package com.orange.talent.api.modules.person.service.impl;

import com.orange.talent.api.model.Person;
import com.orange.talent.api.model.Ticket;
import com.orange.talent.api.modules.person.dto.PersonDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;
import com.orange.talent.api.modules.person.mapper.PersonMapper;
import com.orange.talent.api.modules.person.repository.PersonRepository;
import com.orange.talent.api.modules.person.service.PersonService;
import com.orange.talent.api.modules.ticket.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final TicketService ticketService;

    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    public PersonServiceImpl(PersonRepository personRepository, TicketService ticketService) {
        this.personRepository = personRepository;
        this.ticketService = ticketService;
    }

    @Override
    public PersonDTO createPersonAndBet(PersonDTO personDTO) {

        final var person = verifyIfIsAlreadyRegistered(personDTO);

        if(person.getTickets() == null) {
            person.setTickets(List.of(new Ticket()));
        }

        final var saved = this.personRepository.save(person);

        return personMapper.toDTO(saved);
    }

    @Override
    public PersonDTO findBetByEmail(String email) throws PersonNotFoundException {
        return this.personRepository.findByEmail(email)
                .map(person -> {
                    person.getTickets().sort(Comparator.comparing(Ticket::getCreatedAt));
                    return person;
                })
                .map(personMapper::toDTO)
                .orElseThrow(() -> new PersonNotFoundException(email));
    }

    private Person verifyIfIsAlreadyRegistered(PersonDTO personDTO) {
        return personRepository.findByEmail(personDTO.getEmail())
                .map(person -> {
                    this.ticketService.verifyIfTicketNumberAlreadyExistsAndCreateNew(person.getTickets());
                    return person;
                }) // get and create ticket
                .orElseGet(() -> personMapper.toModel(personDTO)); // quando não existe email
    }
}
