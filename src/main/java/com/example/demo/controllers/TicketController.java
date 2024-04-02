package com.example.demo.controllers;

import com.example.demo.models.dtos.TicketDTO;
import com.example.demo.models.entities.Ticket;
import com.example.demo.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor

        //Сделать все через REST
// Перевести все на микросервисы (eureka, gateway и тд)
        // Нотификации вынести в отдельный микросервис и общаться с ним через брокер
        // Базу данных и брокер помещаем в докер компоуз
        // Покрыть тестами (юниты и интеграционные, использовать тест контейнеры)

public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/allTickets")
    public ResponseEntity<?> allTicketsOverview(HttpServletRequest request) {
        return ResponseEntity.ok(ticketService.getAllTickets(request));
    }
    @GetMapping("/createTicket")
    public ResponseEntity<TicketDTO> createTicket (){
        return ResponseEntity.ok(new TicketDTO());
    }

    @InitBinder("newTicket")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("attachments","category");
    }
    @PostMapping(value = "/createTicket")
    public ResponseEntity<String> createTicket(HttpServletRequest request,
                                               @Valid @ModelAttribute("newTicket") Ticket newTicket,
                                               @RequestParam(name = "attachments",required = false) MultipartFile[] attachments,
                                               @RequestParam(name = "category") Integer categoryId,
                                               @RequestParam(required = false) String comment){
        ticketService.createTicket(newTicket, request, attachments, comment, categoryId);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/ticketOverview/{id}")
    public ResponseEntity<?> ticketOverview(@PathVariable Integer id, HttpServletRequest request){
        return ResponseEntity.ok(ticketService.getTicket(id, request));
    }

    @GetMapping("/editTicket/{id}")
    public ResponseEntity<?> editTicket(@PathVariable Integer id){
        return ResponseEntity.ok(ticketService.getTicketEditDTO(id));
    }

    @PostMapping(value = "/editTicket/{id}")
    public ResponseEntity<String> saveEditedTicket(HttpServletRequest request,
                               @Valid @ModelAttribute("newTicket") Ticket editedTicket,
                               @RequestParam(name = "attachments",required = false) MultipartFile[] attachments,
                               @RequestParam(name = "category") Integer categoryId,
                                   RedirectAttributes attributes) {
        ticketService.editTicket(editedTicket, request, attachments, categoryId);
        attributes.addAttribute("id", editedTicket.getId());
        return ResponseEntity.ok("Ticket saved successfully");
    }

    @PostMapping("/transmitStatus/{ticketId}")
    public ResponseEntity<String> transmitStatus(@PathVariable Integer ticketId,
                                @RequestParam String selectedAction,
                                 HttpServletRequest request){
        ticketService.transmitStatus(ticketId, selectedAction, request);
        return ResponseEntity.ok("File downloaded");
    }
}
