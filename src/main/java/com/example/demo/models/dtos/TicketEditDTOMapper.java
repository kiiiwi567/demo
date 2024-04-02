package com.example.demo.models.dtos;

import com.example.demo.models.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class TicketEditDTOMapper implements Function<Ticket, TicketEditDTO> {
    @Override
    public TicketEditDTO apply(Ticket ticket) {
        TicketEditDTO dto = new TicketEditDTO();

        dto.setId(ticket.getId());
        dto.setName(ticket.getName());
        dto.setCategoryId(ticket.getCategory().getId());
        dto.setDescription(ticket.getDescription());
        dto.setUrgency(ticket.getUrgency());
        dto.setDesiredResolutionDate(ticket.getDesiredResolutionDate());
        if(ticket.getAttachments().size() != 0){
            dto.setAttachments(ticket.getAttachments());
        }
        return dto;
    }
}
