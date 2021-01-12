package com.orange.talent.api.modules.person.controller;

import com.orange.talent.api.modules.person.dto.request.PersonRequestDTO;
import com.orange.talent.api.modules.person.dto.response.PersonResponseDTO;
import com.orange.talent.api.modules.person.exception.PersonNotFoundException;
import com.orange.talent.api.modules.person.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/person")
@Api(value = "API REST Lottery")
@CrossOrigin(origins = "*")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Cria uma nova pessoa com uma aposta ou recupera a pessoa e cria uma nova aposta.")
    public PersonResponseDTO createPersonAndBet(@RequestBody @Valid PersonRequestDTO requestDTO) {
        return this.personService.getOrCreate(requestDTO);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Lista todas as apostas em ordem de criação de uma determinada pessoa.")
    public PersonResponseDTO findBetByEmail(@PathVariable("email") String email) throws PersonNotFoundException {
        return this.personService.findBetByEmail(email);
    }

}
