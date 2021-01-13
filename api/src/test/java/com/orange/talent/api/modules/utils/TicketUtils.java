package com.orange.talent.api.modules.utils;

import com.orange.talent.api.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketUtils {

    public static Ticket generateTicket() {
        final var ticket = new Ticket();

        ticket.setId(1L);
        ticket.setRandomNumber(1);
        ticket.setCreatedAt(LocalDateTime.MIN);

        return ticket;
    }

}
