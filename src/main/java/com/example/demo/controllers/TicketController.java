package com.example.demo.controllers;

import com.example.demo.config.JwtService;
import com.example.demo.models.Attachment;
import com.example.demo.models.Ticket;
import com.example.demo.services.AttachmentService;
import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final JwtService jwtService;
    private final AttachmentService attachmentService;

    @GetMapping("/allTickets")
    public String allTicketsOverview(Model model, HttpServletRequest request){

        model.addAttribute("tickets", ticketService.getAllowedTickets(request));
        model.addAttribute("currentUserEmail", jwtService.extractUsername(jwtService.extractTokenFromRequest(request)));
        model.addAttribute("currentUserRole", jwtService.extractRole(jwtService.extractTokenFromRequest(request)));
        return "allTickets";
    }
    @GetMapping("/createTicket")
    public String createTicket (Model model, HttpServletRequest request) throws IllegalAccessException{
        if (!Objects.equals(jwtService.extractRole(jwtService.extractTokenFromRequest(request)), "Engineer")){
            model.addAttribute("newTicket", new Ticket());
            return "createTicket";
        }
        else {
            throw new IllegalAccessException("Engineer tried to access ticket creation page");
        }
    }

    @InitBinder("newTicket")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("attachments","category");
    }
    @PostMapping(value = "/createTicket")
    public String createTicket(HttpServletRequest request,
                               @Valid @ModelAttribute("newTicket") Ticket newTicket,
                               @RequestParam(name = "attachments",required = false) MultipartFile[] attachments,
                               @RequestParam(name = "category") Integer categoryId,
                               @RequestParam(required = false) String comment){
        ticketService.createTicket(newTicket, request, attachments, comment, categoryId);
        return "redirect:/allTickets";
    }

    @GetMapping("/ticketOverview/{id}")
    public String ticketOverview(@PathVariable Integer id, Model model){
        model.addAttribute("ticket", ticketService.getTicketForOverviewById(id));
        return "ticketOverview";
    }

    @GetMapping("/editTicket/{id}")
    public String editTicket(@PathVariable Integer id, Model model){
        Ticket ticket = ticketService.getTicketForOverviewById(id);
        model.addAttribute("newTicket", ticket);
        return "editTicket";
    }

    @PostMapping(value = "/editTicket/{id}")
    public String saveEditedTicket(HttpServletRequest request,
                               @Valid @ModelAttribute("newTicket") Ticket editedTicket,
                               @RequestParam(name = "attachments",required = false) MultipartFile[] attachments,
                               @RequestParam(name = "category") Integer categoryId,
                                   RedirectAttributes attributes) {
        ticketService.editTicket(editedTicket, request, attachments, categoryId);
        attributes.addAttribute("id", editedTicket.getId());
        return "redirect:/ticketOverview/{id}";
    }

    @PostMapping(value = "/leaveComment/{id}")
    public String leaveComment(HttpServletRequest request,
                               @RequestParam String commentText,
                               @PathVariable Integer id,
                                RedirectAttributes attributes){
        ticketService.leaveComment(request, commentText, id);
        attributes.addAttribute("id", id);
        return "redirect:/ticketOverview/{id}";
    }

    @GetMapping("/download/{ticketId}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,
                                                 @PathVariable Integer ticketId) {
        Attachment attachment = attachmentService.getAttachmentByNameAndTicketId(fileName, ticketId);
        ByteArrayResource resource = new ByteArrayResource(attachment.getContents());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                .body(resource);
    }

    @PostMapping("/transmitStatus/{ticketId}")
    public String transmitStatus(@PathVariable Integer ticketId,
                                @RequestParam String selectedAction,
                                 HttpServletRequest request){
        ticketService.transmitStatus(ticketId, selectedAction, request);
        return "redirect:/allTickets";
    }
}
