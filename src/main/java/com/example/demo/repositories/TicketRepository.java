package com.example.demo.repositories;

import com.example.demo.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository <Ticket, Integer> {
    @Query(nativeQuery = true, value = "select * from ticket")
    List<Ticket> getAllTickets();
}
