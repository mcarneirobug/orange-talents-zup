package com.orange.talent.api.modules.utils;

import com.orange.talent.api.model.Person;
import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;

import java.util.Collections;

public class PersonUtils {

    private static final long PERSON_ID = 1L;
    private static final String EMAIL = "matheus@gmail.com";

    public static PersonResponseDTO generatePersonResponseDTO() {
        return PersonResponseDTO
                .builder()
                .tickets(Collections.singletonList(TicketUtils.generateTicket()))
                .build();
    }

    public static PersonRequestDTO generatePersonRequestDTO() {
        return PersonRequestDTO
                .builder()
                .email(EMAIL)
                .build();
    }

    public static Person generatePerson() {
        return Person.
                builder()
                .id(PERSON_ID)
                .email(EMAIL)
                .tickets(Collections.singletonList(TicketUtils.generateTicket()))
                .build();
    }
}
