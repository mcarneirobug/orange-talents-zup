package com.orange.talent.api.modules.person.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends Exception {

    public PersonNotFoundException(String email) {
        super(String.format("Person with e-mail %s not found in the system.", email));
    }

}
