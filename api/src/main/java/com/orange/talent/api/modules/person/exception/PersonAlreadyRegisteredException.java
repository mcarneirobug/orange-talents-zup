package com.orange.talent.api.modules.person.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonAlreadyRegisteredException extends Exception {

    public PersonAlreadyRegisteredException(String email) {
        super(String.format("Person with e-mail %s already registered in the system.", email));
    }
}
