package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.*;
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
            List<Attachment> attachments = addAttachmentsToList(attachmentFiles, newTicket.getId());
            for (Attachment attachment : attachments){
                entityManager.persist(attachment);
            }
        }

        if (commentText!=null && !commentText.isEmpty() ){
            addComment(commentText, newTicket.getId(), ticketCreator);
        }
    }

    @Transactional
    public void editTicket(Ticket newTicket,
                             HttpServletRequest request,
                             MultipartFile[] attachmentFiles,
                             Integer categoryId) {
        Ticket oldTicket = ticketRepository.getTicketForOverviewById(newTicket.getId());
        String currentUserEmail = jwtService.extractUsername(jwtService.extractTokenFromRequest(request));
        userRepository.findByEmail(currentUserEmail).orElseThrow(() ->
                new NoSuchElementException("Ticket is trying to be edited by a user, who isn't in a DB: " + currentUserEmail));
        newTicket.setCategory(categoryRepository.getCategoryById(categoryId));
        newTicket.setCreatedOn(oldTicket.getCreatedOn());
        newTicket.setState(oldTicket.getState());
        newTicket.setApprover(oldTicket.getApprover());
        newTicket.setAssignee(oldTicket.getAssignee());
        newTicket.setOwner(oldTicket.getOwner());
        newTicket.setHistoryRecords(oldTicket.getHistoryRecords());
        newTicket.setComments(oldTicket.getComments());

        List<Attachment> ticketAttachmentList = addAttachmentsToList(attachmentFiles, newTicket.getId());
        removeNoLongerValidAttachments(ticketAttachmentList, oldTicket.getAttachments());
        ticketAttachmentList = replaceEmptyAttachments(ticketAttachmentList, oldTicket.getAttachments());

        newTicket.setAttachments(ticketAttachmentList);
        entityManager.merge(newTicket);
    }

    public List<Attachment> addAttachmentsToList(MultipartFile[] attachmentFiles, Integer ticketId){
        List<Attachment> attachmentList = new ArrayList<>();
        for (MultipartFile attachmentFile : attachmentFiles) {
            attachmentList.add(MultipartToAttachment(attachmentFile, ticketId));
        }
        return attachmentList;
    }

    public Attachment MultipartToAttachment(MultipartFile file, Integer ticketId){
        Attachment attachment = new Attachment();
        try {
            attachment.setContents(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        attachment.setTicketId(ticketId);
        attachment.setName(file.getOriginalFilename());
        return attachment;
    }

    public void removeNoLongerValidAttachments(List<Attachment> validAttachments, List<Attachment> currentAttachments ){
        for (Attachment attachmentToCheck : currentAttachments) {
            if(validAttachments.stream().noneMatch(attachment -> attachment.getName().equals(attachmentToCheck.getName()))){
                entityManager.remove(attachmentToCheck);
            }
        }
    }

    public List<Attachment> replaceEmptyAttachments(List<Attachment> attachmentsToFix, List<Attachment> oldAttachments) {
        List<Attachment> correctList = new ArrayList<>();
        for (Attachment attachment : attachmentsToFix) {
            if (attachment.getContents().length == 0) {
                for (Attachment oldAttachment : oldAttachments) {
                    if (oldAttachment.getName().equals(attachment.getName())) {
                        correctList.add(oldAttachment);
                        break;
                    }
                }
            }
            else{
                correctList.add(attachment);
            }
        }
        return correctList;
    }
    @Transactional
    public void leaveComment(HttpServletRequest request, String commentText, Integer ticketId) {
        String currentUserEmail = jwtService.extractUsername(jwtService.extractTokenFromRequest(request));
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow(() ->
                new NoSuchElementException("Ticket is trying to be edited by a user, who isn't in a DB: " + currentUserEmail));
        addComment(commentText, ticketId, currentUser);
    }

    public void addComment(String commentText, Integer ticketId, User currentUser){
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setTicketId(ticketId);
        comment.setUser(currentUser);
        entityManager.merge(comment);
    }

    public Ticket getTicketForOverviewById(Integer ticketId) {
        return ticketRepository.getTicketForOverviewById(ticketId);
    }
}
