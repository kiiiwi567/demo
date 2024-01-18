package com.example.demo.controllers;

import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/allTickets")
    public String allTicketsOverview(Model model, HttpServletRequest request){

        model.addAttribute("tickets", ticketService.getAllowedTickets(request));
        return "allTickets";
    }
}
