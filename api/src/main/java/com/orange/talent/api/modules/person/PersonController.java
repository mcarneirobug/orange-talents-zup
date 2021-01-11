package com.orange.talent.api.modules.person;

import com.orange.talent.api.modules.person.dto.PersonDTO;
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
    public PersonDTO createPersonAndBet(@RequestBody @Valid PersonDTO personDTO) {
        return this.personService.createPersonAndBet(personDTO);
    }

    @GetMapping("/{email}")
    public PersonDTO findBetByEmail(@PathVariable("email") String email) throws PersonNotFoundException {
        return this.personService.findBetByEmail(email);
    }

}
