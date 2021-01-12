package com.orange.talent.api.modules.utils;

import com.orange.talent.api.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketUtils {

    public static Ticket generateTicket() {
        return Ticket
                .builder()
                .id(1L)
                .randomNumber(1)
                .createdAt(LocalDateTime.MIN)
                .build();
    }

}
