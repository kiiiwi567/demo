package com.example.demo.services;

import com.example.demo.config.JwtService;
import com.example.demo.models.dtos.*;
import com.example.demo.models.entities.*;
import com.example.demo.models.enums.TicketState;
import com.example.demo.repositories.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final JwtService jwtService;
    private final MailSenderService mailSenderService;
    private final AttachmentService attachmentService;
    private final StatisticsService statisticsService;

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FeedbackRepository feedbackRepository;

    private final TicketDTOMapper ticketDTOMapper;
    private final TicketFullDTOMapper ticketFullDTOMapper;
    private final TicketEditDTOMapper ticketEditDTOMapper;
    private final TicketCreateDTOUnmapper ticketCreateDTOUnmapper;
    private final TicketEditDTOUnmapper ticketEditDTOUnmapper;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Ticket> getAllTickets(){
        return ticketRepository.getAllTickets();
    }

    public ResponseEntity<Map<String, Object>> getAllowedTickets(HttpServletRequest request){
        String jwt = jwtService.extractTokenFromRequest(request);
        String role = jwtService.extractRole(jwt);
        String email = jwtService.extractUsername(jwt);
        List<Ticket> tickets;
        switch (role) {
            case "Employee" -> tickets = ticketRepository.getAllTicketsForEmployee(email);
            case "Manager" -> tickets = ticketRepository.getAllTicketsForManager(email);
            case "Engineer" -> tickets = ticketRepository.getAllTicketsForEngineer(email);
            default -> throw new IllegalStateException("Unexpected value: " + role);
        }
        List<String> bestCategories = statisticsService.figureCategoryWhereBest(email);
        List<TicketDTO> ticketDTOs = tickets.stream()
                .map(ticketDTOMapper)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("ticketDTOs", ticketDTOs);
        response.put("bestCategories", bestCategories);

        return ResponseEntity.ok().body(response);
    }

    public Ticket getTicketById(Integer ticketId) {
        return ticketRepository.getTicketForOverviewById(ticketId);
    }

    public TicketFullDTO getTicketFullDTO(Integer ticketId) {
        return ticketFullDTOMapper.apply(getTicketById(ticketId));
    }

    public TicketEditDTO getTicketEditDTO(Integer ticketId) {
        return ticketEditDTOMapper.apply(getTicketById(ticketId));
    }

    @Transactional
    public void createTicket(TicketCreateDTO dto,
                             MultipartFile[] ticketFiles,
                             HttpServletRequest request) {
        User ticketCreator = jwtService.getUserFromRequest(request);
        Ticket newTicket = ticketCreateDTOUnmapper.apply(dto);

        newTicket.setCategory(categoryRepository.getCategoryById(dto.getCategory()));
        newTicket.setOwner(ticketCreator);
        entityManager.persist(newTicket);

        additionalTicketProcessing (ticketFiles, newTicket.getId(), ticketCreator, dto);
    }

    public void additionalTicketProcessing (MultipartFile[] ticketFiles, Integer ticketId, User ticketCreator, TicketCreateDTO dto){
        if (ticketFiles != null) {
            attachmentService.addAttachmentsToNewTicket(ticketFiles, ticketId, ticketCreator);
        }

        if (dto.getComment()!=null && !dto.getComment().isEmpty() ){
            Comment comment = new Comment(ticketCreator, dto.getComment(), ticketId);
            entityManager.persist(comment);
        }

        History crRecord = new History(ticketId,
                "Ticket is created",
                ticketCreator,
                "Ticket is created");
        entityManager.persist(crRecord);

        mailSenderService.sendNewMail(userRepository.findEmailsByRole("Manager"),
                "New ticket for approval",
                "Dear Managers,<br><br>" + "New ticket " + "<a href=\"http://localhost:8080/ticketOverview/" + ticketId + "\">" + ticketId + "</a>" + " is waiting for your approval");
    }

    @Transactional
    public void createUnauthTicket(TicketCreateDTO dto,
                             MultipartFile[] ticketFiles) {
        User manager1 = userRepository.findUserById(3);
        Ticket newTicket = ticketCreateDTOUnmapper.apply(dto);
        newTicket.setName(dto.getName() + "(from " + dto.getOwnerEmail() + ")");
        newTicket.setCategory(categoryRepository.getCategoryById(dto.getCategory()));
        newTicket.setOwner(manager1);
        entityManager.persist(newTicket);

        additionalTicketProcessing (ticketFiles, newTicket.getId(), manager1, dto);
    }

    @Transactional
    public void editTicket(TicketEditDTO dto,
                           MultipartFile[] attachmentFiles,
                           HttpServletRequest request) {
        Ticket newTicket = ticketEditDTOUnmapper.apply(dto);
        Ticket oldTicket = ticketRepository.getTicketForOverviewById(dto.getId());
        BeanUtils.copyProperties(oldTicket, newTicket, "category", "attachments","id", "name", "description","urgency","desiredResolutionDate");
        newTicket.setCategory(categoryRepository.getCategoryById(dto.getCategoryId()));

        User editingUser = jwtService.getUserFromRequest(request);
        List<Attachment> finAtt = new ArrayList<>(attachmentService.processAttachmentsForEdition(attachmentFiles,newTicket.getId(),oldTicket.getAttachments(),dto.getAttachments(),editingUser));

        entityManager.detach(oldTicket);

        newTicket.setAttachments(finAtt);

        History record = new History(newTicket.getId(),
                                    "Ticket edited",
                                    jwtService.getUserFromRequest(request),
                                    "Ticket edited");
        entityManager.merge(record);
        ticketRepository.save(newTicket);
    }


    @Transactional
    public CommentDTO leaveComment(HttpServletRequest request, String commentText, Integer ticketId) {
        User currentUser = jwtService.getUserFromRequest(request);
        Comment comment = new Comment(currentUser, commentText, ticketId);
        entityManager.merge(comment);
        return new CommentDTO(currentUser.getFirstName(), commentText, LocalDateTime.now());
    }

    @Transactional
    public String transmitStatus(Integer ticketId, String action, HttpServletRequest request) {
        Ticket ticket = getTicketById(ticketId);
        String previousStatus = String.valueOf(ticket.getState());
        User user = jwtService.getUserFromRequest(request);
        String selectedAction = action.substring(1, action.length() - 1);
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
//                mailSenderService.sendNewMail(Stream.concat(Stream.of(owner.getEmail()), Stream.of(userRepository.findEmailsByRole("Engineer"))).toArray(String[]::new),
//                        "Ticket was approved",
//                        "Dear Users,<br><br>" + "Ticket " + ticketLink + " was approved by a manager"
//                );
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
        return ticket.getState().toString();
    }
    @Transactional
    public HistoryDTO leaveFeedback(Integer ticketId, JsonNode json, HttpServletRequest request) {
        String commentText = json.get("commentText").asText();
        Integer rate = json.get("starRating").asInt();
        Optional<Feedback> existingFeedback = feedbackRepository.getFeedbackByTicketId(ticketId);
        User currentUser = jwtService.getUserFromRequest(request);
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
        User assignee = getTicketById(ticketId).getAssignee();
        mailSenderService.sendNewMail(new String[]{assignee.getEmail()},
                "Feedback was provided",
                "Dear " + assignee.getFirstName() + " " + assignee.getLastName() + "<br><br>" + "The feedback was provided on ticket " + "<a href=\"http://localhost:4200/ticketOverview/" + ticketId + "\">" + ticketId + "</a>"
        );

        entityManager.merge(record);
        return new HistoryDTO(LocalDateTime.now(), record.getAction(), currentUser.getFirstName(), record.getDescription());
    }

}
