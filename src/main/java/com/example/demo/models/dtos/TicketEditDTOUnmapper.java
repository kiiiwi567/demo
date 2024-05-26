package com.example.demo.models.dtos;

import com.example.demo.models.entities.Comment;
import com.example.demo.models.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class TicketEditDTOUnmapper implements Function<TicketEditDTO, Ticket> {
    @Override
    public Ticket apply(TicketEditDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setName(dto.getName());
        ticket.setDescription(dto.getDescription());
        ticket.setUrgency(dto.getUrgency());
        ticket.setDesiredResolutionDate(dto.getDesiredResolutionDate());

        return ticket;
    }
}