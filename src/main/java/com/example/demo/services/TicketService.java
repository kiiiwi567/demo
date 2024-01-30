package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.Ticket;
import com.example.demo.models.enums.TicketState;
import com.example.demo.repositories.TicketRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;
    public List<Ticket> getAllowedTickets(HttpServletRequest request){
        String jwt = jwtService.extractTokenFromRequest(request);
        String role = jwtService.extractRole(jwt);
        String email = jwtService.extractUsername(jwt);
        return switch (role) {
            case "Employee" -> ticketRepository.getAllTicketsForEmployee(email);
            case "Manager" -> ticketRepository.getAllTicketsForManager(email);
            case "Engineer" -> ticketRepository.getAllTicketsForEngineer(email);
            default -> ticketRepository.getAllTickets();
        };
    }
    @Transactional
    public void createTicket(Ticket newTicket, HttpServletRequest request) {
        String ownerEmail = jwtService.extractUsername(jwtService.extractTokenFromRequest(request));
        newTicket.setOwner(userRepository.findByEmail(ownerEmail).orElseThrow(() ->
                new NoSuchElementException("Ticket is trying to be created by a user, who isn't in a DB: " + ownerEmail)));
        entityManager.persist(newTicket);
    }
}
