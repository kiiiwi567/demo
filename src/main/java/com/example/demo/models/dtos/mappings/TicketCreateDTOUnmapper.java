package com.example.demo.models.dtos.mappings;

import com.example.demo.models.dtos.TicketCreateDTO;
import com.example.demo.models.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class TicketCreateDTOUnmapper implements Function<TicketCreateDTO, Ticket> {
    @Override
    public Ticket apply(TicketCreateDTO dto) {
        Ticket ticket = new Ticket();

        ticket.setName(dto.getName());
        ticket.setDescription(dto.getDescription());
        ticket.setUrgency(dto.getUrgency());
        ticket.setDesiredResolutionDate(dto.getDesiredResolutionDate());

        return ticket;
    }
}
