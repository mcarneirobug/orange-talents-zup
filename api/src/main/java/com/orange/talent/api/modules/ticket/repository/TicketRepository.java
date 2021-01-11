package com.orange.talent.api.modules.ticket.repository;

import com.orange.talent.api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = "FROM Ticket t "
            + "WHERE t.randomNumber = :randomNumber "
            + "AND t.createdAt IS NOT NULL"
    )
    Optional<Ticket> findRandomTicket(@Param("randomNumber") Integer randomNumber);

}
