package com.orange.talent.api.modules.utils;

import com.orange.talent.api.model.Person;
import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;

import java.util.Collections;

public class PersonUtils {

    private static final long PERSON_ID = 1L;
    private static final String EMAIL = "matheus@gmail.com";

    public static PersonResponseDTO generatePersonResponseDTO() {
        final var personResponseDTO = new PersonResponseDTO();

        personResponseDTO.setTickets(Collections.singletonList(TicketUtils.generateTicket()));

        return personResponseDTO;
    }

    public static PersonRequestDTO generatePersonRequestDTO() {
        final var personRequestDTO = new PersonRequestDTO();

        personRequestDTO.setEmail(EMAIL);

        return personRequestDTO;
    }

    public static Person generatePerson() {
        final var person = new Person();

        person.setId(PERSON_ID);
        person.setEmail(EMAIL);
        person.setTickets(Collections.singletonList(TicketUtils.generateTicket()));

        return person;
    }
}
