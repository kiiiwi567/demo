package com.example.demo.controllers;

import com.example.demo.models.dtos.TicketCreateDTO;
import com.example.demo.models.dtos.TicketEditDTO;
import com.example.demo.models.dtos.TicketFullDTO;
import com.example.demo.services.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor

        //Сделать все через REST
// Перевести все на микросервисы (eureka, gateway и тд)
        // Нотификации вынести в отдельный микросервис и общаться с ним через брокер
        // Базу данных и брокер помещаем в докер компоуз
        // Покрыть тестами (юниты и интеграционные, использовать тест контейнеры)

public class TicketController {
    private final TicketService ticketService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @GetMapping("/allTickets")
    public ResponseEntity<?> allTicketsOverview(HttpServletRequest request) {
        return ticketService.getAllowedTickets(request);
    }

    @PostMapping(value = "/createTicket")
    public ResponseEntity<String> createTicket(HttpServletRequest request,
                                               @RequestParam(name = "newTicket") String newTicketJson,
                                               @RequestParam(name = "ticketFiles",required = false) MultipartFile[] ticketFiles){
        try {
            TicketCreateDTO newTicket = objectMapper.readValue(newTicketJson, TicketCreateDTO.class);
            ticketService.createTicket(newTicket, ticketFiles, request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/createUnauthTicket")
    public ResponseEntity<String> createUnauthTicket(@RequestParam(name = "newTicket") String newTicketJson,
                                                    @RequestParam(name = "ticketFiles",required = false) MultipartFile[] ticketFiles){
        try {
            TicketCreateDTO newTicket = objectMapper.readValue(newTicketJson, TicketCreateDTO.class);
            ticketService.createUnauthTicket(newTicket, ticketFiles);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ticketOverview/{id}")
    public ResponseEntity<TicketFullDTO> ticketOverview(@PathVariable Integer id, HttpServletRequest request){
        return ResponseEntity.ok().body(ticketService.getTicketFullDTO(id));
    }

    @GetMapping("/editTicket/{id}")
    public ResponseEntity<TicketEditDTO> editTicket(@PathVariable Integer id){
        return ResponseEntity.ok().body(ticketService.getTicketEditDTO(id));
    }

    @PostMapping(value = "/editTicket/{id}")
    public ResponseEntity<String> saveEditedTicket(HttpServletRequest request,
                                                   @RequestParam(name = "editTicket") String editedTicketJson,
                                                   @RequestParam(name = "newFiles",required = false) MultipartFile[] ticketFiles) {
        try {
            TicketEditDTO dto = objectMapper.readValue(editedTicketJson, TicketEditDTO.class);
            ticketService.editTicket(dto, ticketFiles, request);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transmitStatus/{ticketId}")
    public ResponseEntity<String> transmitStatus(@PathVariable Integer ticketId,
                                                 @RequestBody String status,
                                                 HttpServletRequest request){
        String resp = ticketService.transmitStatus(ticketId, status, request);
        return ResponseEntity.ok().body("{\"status\":\"" + resp + "\"}");
    }
}
