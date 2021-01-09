package com.orange.talent.api.modules.person;

import com.orange.talent.api.modules.person.dto.PersonDTO;
import com.orange.talent.api.modules.person.exception.PersonAlreadyRegisteredException;
import com.orange.talent.api.modules.person.service.PersonService;
import javassist.NotFoundException;
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
    public PersonDTO createPerson(@RequestBody @Valid PersonDTO personDTO) throws PersonAlreadyRegisteredException {
        return this.personService.createPerson(personDTO);
    }

    @GetMapping("/{email}")
    public PersonDTO findByEmail(@PathVariable("email") String email) throws NotFoundException {
        return this.personService.findByEmail(email);
    }

}
