package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.Attachment;
import com.example.demo.models.Comment;
import com.example.demo.models.Ticket;
import com.example.demo.models.User;
import com.example.demo.models.enums.TicketState;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.TicketRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
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
    public void createTicket(Ticket newTicket,
                             HttpServletRequest request,
                             MultipartFile[] attachmentFiles,
                             String commentText,
                             Integer categoryId) {
        String ownerEmail = jwtService.extractUsername(jwtService.extractTokenFromRequest(request));
        User ticketCreator = userRepository.findByEmail(ownerEmail).orElseThrow(() ->
                new NoSuchElementException("Ticket is trying to be created by a user, who isn't in a DB: " + ownerEmail));
        newTicket.setCategory(categoryRepository.getCategoryById(categoryId));
        newTicket.setOwner(ticketCreator);
        entityManager.persist(newTicket);

        if (attachmentFiles != null){
            for (MultipartFile attachmentFile : attachmentFiles) {
                Attachment attachment = new Attachment();
                try {
                    attachment.setContents(attachmentFile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                attachment.setTicketId(newTicket.getId());
                attachment.setName(attachmentFile.getOriginalFilename());
                entityManager.persist(attachment);
            }
        }

        if (!commentText.isEmpty()){
            Comment comment = new Comment();
            comment.setText(commentText);
            comment.setTicketId(newTicket.getId());
            comment.setUser(ticketCreator);
            entityManager.persist(comment);
        }
    }

    public Ticket getTicketForOverviewById(Integer ticketId) {
        return ticketRepository.getTicketForOverviewById(ticketId);
    }
}
