package com.example.demo.controllers;

import com.example.demo.models.dtos.CommentDTO;
import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommentController {
    private final TicketService ticketService;
    @PostMapping(value = "/leaveComment/{id}")
    public ResponseEntity<CommentDTO> leaveComment(HttpServletRequest request,
                                                   @RequestBody String commentText,
                                                   @PathVariable Integer id){
        return ResponseEntity.ok().body(ticketService.leaveComment(request, commentText, id));
    }
}
