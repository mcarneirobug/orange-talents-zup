package com.orange.talent.api.modules.person.service;

import com.orange.talent.api.ApiApplication;
import com.orange.talent.api.model.Person;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;
import com.orange.talent.api.modules.person.repository.PersonRepository;
import com.orange.talent.api.modules.ticket.service.TicketService;
import com.orange.talent.api.modules.utils.PersonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class PersonServiceTest {

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private PersonService personService;

    @Test
    void shouldCreatePersonAndBetWhenPersonIsInformed() {
        // given
        final var person = PersonUtils.generatePerson();
        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        // when
        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.personRepository.save(any(Person.class))).thenReturn(person);

        // then
        final var personCreatedDTO = this.personService.getOrCreate(personRequestDTO);

        assertNotNull(personCreatedDTO);
        assertThat(personCreatedDTO, is(equalTo(personResponseDTO)));

        verify(this.personRepository, times(1)).findByEmail(anyString());
        verify(this.personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void shouldFindBetByEmailWhenEmailIsInformed() throws PersonNotFoundException {

        final var person = PersonUtils.generatePerson();
        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));

        final var foundPersonDTO = this.personService.findBetByEmail(person.getEmail());

        assertNotNull(foundPersonDTO);
        assertThat(foundPersonDTO, is(equalTo(personResponseDTO)));

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void shouldFindBetByEmailThenAnExceptionShouldBeThrown() {

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        final var exception = assertThrows(PersonNotFoundException.class,
                () -> this.personService.findBetByEmail("matheus@gmail.com"),
                "Deve retornar um PersonNotFoundException!");

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Person with e-mail matheus@gmail.com not found in the system."));

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void shouldVerifyIfTicketNumberAlreadyExists() {

        final var person = PersonUtils.generatePerson();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));
        doNothing().when(this.ticketService).verifyIfTicketNumberAlreadyExistsAndCreateNew(anyList());

        final var personFound = this.personService.getOrCreate(personRequestDTO);

        verify(this.personRepository, times(1)).findByEmail(anyString());
        verify(this.ticketService, times(1)).verifyIfTicketNumberAlreadyExistsAndCreateNew(anyList());
    }

    @Test
    void shouldCreatePersonAndTicketWhenEmailDoesExists() {

        final var person = PersonUtils.generatePerson();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();
        person.setTickets(null);

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.personRepository.save(any(Person.class))).thenReturn(person);

        final var response = this.personService.getOrCreate(personRequestDTO);

        assertNotNull(response);

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }



}
