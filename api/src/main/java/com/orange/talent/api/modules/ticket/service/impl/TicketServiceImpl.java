package com.orange.talent.api.modules.ticket.service.impl;

import com.orange.talent.api.model.Ticket;
import com.orange.talent.api.modules.ticket.repository.TicketRepository;
import com.orange.talent.api.modules.ticket.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void verifyIfTicketNumberAlreadyExistsAndCreateNew(List<Ticket> tickets) {

        final var ticket = new Ticket();

        tickets.forEach(t -> {
            if(t.getRandomNumber().equals(ticket.getRandomNumber())) {
                ticket.setRandomNumber(new Random().nextInt(10000000 - 1));
            } else if (ticket.getRandomNumber().equals(t.getRandomNumber())) {
                log.error("Error: Ticket gerado já gerado previamente.");
            }
        });
        tickets.add(ticket);
    }
}
