package com.example.demo.controllers;

import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackController {
    private final TicketService ticketService;

    @PostMapping("/leaveFeedback/{ticketId}")
    public String leaveFeedback(@PathVariable Integer ticketId,
                                @RequestParam Integer star_rating,
                                @RequestParam String commentText,
                                HttpServletRequest request){
        ticketService.leaveFeedback(ticketId, star_rating, commentText, request);
        return "redirect:/allTickets";
    }
}
