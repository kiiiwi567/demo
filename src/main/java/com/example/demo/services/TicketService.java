package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.Ticket;
import com.example.demo.repositories.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JwtService jwtService;
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
}
