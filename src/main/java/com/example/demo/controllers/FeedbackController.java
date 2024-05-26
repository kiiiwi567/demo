package com.example.demo.controllers;

import com.example.demo.models.dtos.HistoryDTO;
import com.example.demo.services.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FeedbackController {
    private final TicketService ticketService;

    @PostMapping("/leaveFeedback/{ticketId}")
    public ResponseEntity<HistoryDTO> leaveFeedback(@PathVariable Integer ticketId,
                                                    @RequestBody JsonNode json,
                                                    HttpServletRequest request){
        return ResponseEntity.ok().body(ticketService.leaveFeedback(ticketId, json, request));
    }
}
