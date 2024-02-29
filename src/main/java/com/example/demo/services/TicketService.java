package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.*;
import com.example.demo.models.enums.TicketState;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.FeedbackRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FeedbackRepository feedbackRepository;
    private final MailSenderService mailSenderService;
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
        User ticketCreator = getUserFromRequest(request);
        newTicket.setCategory(categoryRepository.getCategoryById(categoryId));
        newTicket.setOwner(ticketCreator);
        entityManager.persist(newTicket);

        if (attachmentFiles != null){
            List<Attachment> attachments = addAttachmentsToList(attachmentFiles, newTicket.getId());
            for (Attachment attachment : attachments){
                entityManager.persist(attachment);
                History attRecord = new History(newTicket.getId(),
                        "File is attached",
                        ticketCreator,
                        "File is attached: " + attachment.getName());
                entityManager.persist(attRecord);
            }
        }

        if (commentText!=null && !commentText.isEmpty() ){
            Comment comment = new Comment(ticketCreator, commentText, newTicket.getId());
            entityManager.persist(comment);
        }

        History crRecord = new History(newTicket.getId(),
                "Ticket is created",
                ticketCreator,
                "Ticket is created");
        entityManager.persist(crRecord);

        mailSenderService.sendNewMail(userRepository.findEmailsByRole("Manager"),
                "New ticket for approval",
                "Dear Managers,<br><br>" + "New ticket " + "<a href=\"http://localhost:8080/ticketOverview/" + newTicket.getId() + "\">" + newTicket.getId() + "</a>" + " is waiting for your approval");
    }

    @Transactional
    public void editTicket(Ticket newTicket,
                             HttpServletRequest request,
                             MultipartFile[] attachmentFiles,
                             Integer categoryId) {
        Ticket oldTicket = ticketRepository.getTicketForOverviewById(newTicket.getId());
        newTicket.setCategory(categoryRepository.getCategoryById(categoryId));
        newTicket.setCreatedOn(oldTicket.getCreatedOn());
        newTicket.setState(oldTicket.getState());
        newTicket.setApprover(oldTicket.getApprover());
        newTicket.setAssignee(oldTicket.getAssignee());
        newTicket.setOwner(oldTicket.getOwner());
        newTicket.setHistoryRecords(oldTicket.getHistoryRecords());
        newTicket.setComments(oldTicket.getComments());
        newTicket.setFeedback(oldTicket.getFeedback());

        User editingUser = getUserFromRequest(request);

        List<Attachment> ticketAttachmentList = addAttachmentsToList(attachmentFiles, newTicket.getId());
        removeNoLongerValidAttachments(ticketAttachmentList, oldTicket.getAttachments(), editingUser);
        ticketAttachmentList = replaceEmptyAttachments(ticketAttachmentList, oldTicket.getAttachments(), editingUser);

        newTicket.setAttachments(ticketAttachmentList);

        History record = new History(newTicket.getId(),
                                    "Ticket edited",
                                    getUserFromRequest(request),
                                    "Ticket edited");
        entityManager.merge(record);
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

    public void removeNoLongerValidAttachments(List<Attachment> validAttachments, List<Attachment> currentAttachments, User currentUser){
        for (Attachment attachmentToCheck : currentAttachments) {
            if(validAttachments.stream().noneMatch(attachment -> attachment.getName().equals(attachmentToCheck.getName()))){
                entityManager.remove(attachmentToCheck);

                History attRecord = new History(attachmentToCheck.getTicketId(),
                        "File removed",
                        currentUser,
                        "File removed: " + attachmentToCheck.getName());
                entityManager.persist(attRecord);
            }
        }
    }

    public List<Attachment> replaceEmptyAttachments(List<Attachment> attachmentsToFix, List<Attachment> oldAttachments, User currentUser) {
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
                History attRecord = new History(attachment.getTicketId(),
                        "File added",
                        currentUser,
                        "File added: " + attachment.getName());
                entityManager.persist(attRecord);
            }
        }
        return correctList;
    }
    @Transactional
    public void leaveComment(HttpServletRequest request, String commentText, Integer ticketId) {
        User currentUser = getUserFromRequest(request);
        Comment comment = new Comment(currentUser, commentText, ticketId);
        entityManager.merge(comment);
    }

    public Ticket getTicketForOverviewById(Integer ticketId) {
        return ticketRepository.getTicketForOverviewById(ticketId);
    }

    public User getUserFromRequest (HttpServletRequest request){
        String currentUserEmail = jwtService.extractUsername(jwtService.extractTokenFromRequest(request));
        return userRepository.findByEmail(currentUserEmail).orElseThrow(()->new NoSuchElementException("Couldn't find user in a DB!"));
    }
    @Transactional
    public void transmitStatus(Integer ticketId, String selectedAction, HttpServletRequest request) {
        Ticket ticket = getTicketForOverviewById(ticketId);
        String previousStatus = String.valueOf(ticket.getState());
        User user = getUserFromRequest(request);

        String ticketLink = "<a href=\"http://localhost:8080/ticketOverview/" + ticketId + "\">" + ticketId + "</a>";

        switch (selectedAction) {
            case "Submit" -> {
                ticket.setState(TicketState.New);
                mailSenderService.sendNewMail(userRepository.findEmailsByRole("Manager"),
                        "New ticket for approval",
                        "Dear Managers,<br><br>" + "New ticket " + ticketLink + " is waiting for your approval");
            }
            case "Approve" -> {
                ticket.setState(TicketState.Approved);
                ticket.setApprover(user);
                User owner = ticket.getOwner();
                mailSenderService.sendNewMail(Stream.concat(Stream.of(owner.getEmail()), Stream.of(userRepository.findEmailsByRole("Engineer"))).toArray(String[]::new),
                        "Ticket was approved",
                        "Dear Users,<br><br>" + "Ticket " + ticketLink + " was approved by a manager"
                );
            }
            case "Decline" -> {
                ticket.setState(TicketState.Declined);
                ticket.setApprover(user);
                User owner = ticket.getOwner();
                mailSenderService.sendNewMail(new String[]{owner.getEmail()},
                        "Ticket was declined",
                        "Dear " + owner.getFirstName() + " " + owner.getLastName() + "<br><br>" + "Ticket " + ticketLink + " was declined by a manager"
                );
            }
            case "Cancel" -> {
                ticket.setState(TicketState.Cancelled);
                ticket.setApprover(user);
                User owner = ticket.getOwner();
                mailSenderService.sendNewMail(new String[]{owner.getEmail(),user.getEmail()},
                        "Ticket was cancelled",
                        "Dear " + owner.getFirstName() + " " + owner.getLastName() + "<br><br>" + "Ticket " + ticketLink + " was cancelled by a manager"
                );
            }
            case "Assign to me" -> {
                ticket.setState(TicketState.In_progress);
                ticket.setAssignee(user);
            }
            case "Done" -> {
                ticket.setState(TicketState.Done);
                User owner = ticket.getOwner();
                mailSenderService.sendNewMail(new String[]{owner.getEmail()},
                        "Ticket was done",
                        "Dear " + owner.getFirstName() + " " + owner.getLastName() + "<br><br>" + "Ticket " + ticketLink + " was done by an engineer"
                );
            }
        }
        History record = new History(ticketId,
                                    "Ticket status changed",
                                    user,
                                    "Ticket status changed from '" + previousStatus + "' to '" + ticket.getState().toString() + "'");
        entityManager.merge(ticket);
        entityManager.merge(record);
    }
    @Transactional
    public void leaveFeedback(Integer ticketId, Integer rate, String commentText, HttpServletRequest request) {
        Optional<Feedback> existingFeedback = feedbackRepository.getFeedbackByTicketId(ticketId);
        User currentUser = getUserFromRequest(request);
        if (existingFeedback.isPresent()) {
            Feedback feedback = existingFeedback.get();
            feedback.setRate(rate);
            feedback.setText(commentText);
            feedback.setTimestamp(LocalDateTime.now());
            entityManager.merge(feedback);
        } else {
            Feedback newFeedback = new Feedback(currentUser.getId(), rate, commentText, ticketId);
            entityManager.merge(newFeedback);
        }
        String commentAddition = null;
        if (!commentText.isBlank()){
            commentAddition = "with comment: " + commentText;
        }
        History record = new History(ticketId,
                "Ticket completion rated",
                currentUser,
                "Ticket completion was rated " + "★".repeat(rate) + Objects.toString(commentAddition, ""));
        User assignee = getTicketForOverviewById(ticketId).getAssignee();
        mailSenderService.sendNewMail(new String[]{assignee.getEmail()},
                "Feedback was provided",
                "Dear " + assignee.getFirstName() + " " + assignee.getLastName() + "<br><br>" + "The feedback was provided on ticket " + "<a href=\"http://localhost:8080/ticketOverview/" + ticketId + "\">" + ticketId + "</a>"
        );

        entityManager.merge(record);
    }
}
