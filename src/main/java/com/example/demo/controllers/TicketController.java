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
    public String createTicket(Model model){
        model.addAttribute("newTicket", new Ticket());
        return "createTicket";
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

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Attachment attachment = attachmentService.getAttachmentByName(fileName);
        ByteArrayResource resource = new ByteArrayResource(attachment.getContents());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }
}
