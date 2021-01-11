package com.orange.talent.api.modules.ticket.service;

import com.orange.talent.api.model.Ticket;

import java.util.List;

public interface TicketService {

    void verifyIfTicketNumberAlreadyExistsAndCreateNew(List<Ticket> tickets);

}
