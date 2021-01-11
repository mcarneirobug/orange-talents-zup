package com.orange.talent.api.modules.person;

import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;
import com.orange.talent.api.modules.person.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponseDTO createPersonAndBet(@RequestBody @Valid PersonRequestDTO requestDTO) {
        return this.personService.createPersonAndBet(requestDTO);
    }

    @GetMapping("/{email}")
    public PersonResponseDTO findBetByEmail(@PathVariable("email") String email) throws PersonNotFoundException {
        return this.personService.findBetByEmail(email);
    }

}
