package com.example.demo.controllers;

import com.example.demo.config.JwtService;
import com.example.demo.models.Ticket;
import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final JwtService jwtService;
    @GetMapping("/allTickets")
    public String allTicketsOverview(Model model, HttpServletRequest request){

        model.addAttribute("tickets", ticketService.getAllowedTickets(request));
        model.addAttribute("currentUserEmail", jwtService.extractUsername(jwtService.extractTokenFromRequest(request)));
        model.addAttribute("currentUserRole", jwtService.extractRole(jwtService.extractTokenFromRequest(request)));
        return "allTickets";
    }
    @GetMapping("/createTicket")
    public String createTicket(Model model){
        model.addAttribute("newTicket", new Ticket());
        return "createTicket";
    }

    @PostMapping("/createTicket")
    public String createTicket(HttpServletRequest request, @ModelAttribute("newTicket") Ticket newTicket){
        ticketService.createTicket(newTicket, request);
        return "redirect:/allTickets";
    }
}
