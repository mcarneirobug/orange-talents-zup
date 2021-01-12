package com.orange.talent.api.modules.person.controller;

import com.orange.talent.api.ApiApplication;
import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;
import com.orange.talent.api.modules.person.service.PersonService;
import com.orange.talent.api.modules.utils.PersonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
public class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonService personService;


    @Test
    void shouldCreatedPersonAndBetWhenPersonIsInformed() {

        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        when(this.personService.getOrCreate(any(PersonRequestDTO.class))).thenReturn(personResponseDTO);

        webTestClient
                .post()
                .uri("api/v1/person")
                .bodyValue(personRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PersonResponseDTO.class)
                .isEqualTo(personResponseDTO);

        verify(this.personService, times(1)).getOrCreate(any(PersonRequestDTO.class));
    }

    @Test
    void shouldFindBetByEmailWhenEmailIsInformed() throws PersonNotFoundException {

        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();

        when(this.personService.findBetByEmail(anyString())).thenReturn(personResponseDTO);

        webTestClient
                .get()
                .uri("api/v1/person" + "/{email}", "matheus@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PersonResponseDTO.class)
                .isEqualTo(personResponseDTO);

        verify(this.personService, times(1)).findBetByEmail(anyString());
    }

}
