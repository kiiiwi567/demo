package com.example.demo.services;

import com.example.demo.models.Ticket;
import com.example.demo.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    public List<Ticket> getAllTickets(){
        return ticketRepository.getAllTickets();
    }
}
