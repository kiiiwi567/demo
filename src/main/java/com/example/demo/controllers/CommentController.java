package com.example.demo.controllers;

import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final TicketService ticketService;
    @PostMapping(value = "/leaveComment/{id}")
    public String leaveComment(HttpServletRequest request,
                               @RequestParam String commentText,
                               @PathVariable Integer id,
                               RedirectAttributes attributes){
        ticketService.leaveComment(request, commentText, id);
        attributes.addAttribute("id", id);
        return "redirect:/ticketOverview/{id}";
    }
}
