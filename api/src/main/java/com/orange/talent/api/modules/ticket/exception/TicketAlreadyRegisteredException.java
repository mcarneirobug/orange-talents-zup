package com.orange.talent.api.modules.ticket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketAlreadyRegisteredException extends Exception {

    public TicketAlreadyRegisteredException(Integer random) {
        super(String.format("Ticket with number %s already registered in the system.", random));
    }
}
