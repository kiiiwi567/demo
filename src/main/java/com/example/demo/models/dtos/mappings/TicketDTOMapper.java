package com.example.demo.models.dtos.mappings;

import com.example.demo.models.dtos.TicketDTO;
import com.example.demo.models.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class TicketDTOMapper implements Function<Ticket, TicketDTO> {
    @Override
    public TicketDTO apply(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();

        ticketDTO.setId(ticket.getId());
        ticketDTO.setName(ticket.getName());
        ticketDTO.setCategory(ticket.getCategory().getName());
        ticketDTO.setCreatedOn(ticket.getCreatedOn());
        ticketDTO.setDesiredResolutionDate(ticket.getDesiredResolutionDate());
        ticketDTO.setOwnerEmail(ticket.getOwner().getEmail());
        ticketDTO.setOwnerRole(ticket.getOwner().getRole());

        if (ticket.getApprover() != null) {
            ticketDTO.setApproverEmail(ticket.getApprover().getEmail());
        }

        if (ticket.getAssignee() != null) {
            ticketDTO.setAssigneeEmail(ticket.getAssignee().getEmail());
        }

        ticketDTO.setState(ticket.getState());
        ticketDTO.setUrgency(ticket.getUrgency());

        return ticketDTO;
    }
}
