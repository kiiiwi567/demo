package com.example.demo.models.dtos.mappings;

import com.example.demo.models.dtos.TicketStatDTO;
import com.example.demo.models.entities.History;
import com.example.demo.models.entities.Ticket;
import com.example.demo.models.enums.TicketState;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
@Service
public class TicketStatDTOMapper implements Function<Ticket, TicketStatDTO> {
    @Override
    public TicketStatDTO apply(Ticket ticket) {
        TicketStatDTO dto = new TicketStatDTO();

        dto.setCategory(ticket.getCategory().getName());
        dto.setUrgency(ticket.getUrgency());
        dto.setCreatedOn(ticket.getCreatedOn());
        LocalDateTime from = ticket.getCreatedOn().atTime(12,1);

        LocalDateTime to = ticket.getHistoryRecords().stream()
                .filter(h -> h.getDescription().matches(".*на '(Выполнена|Отменена|Отклонена)'"))
                .findFirst()
                .map(History::getTimestamp)
                .orElse(LocalDateTime.now());

        long secondsBetween = ChronoUnit.SECONDS.between(from, to);
        double daysBetween = secondsBetween / (60.0 * 60.0 * 24.0);
        Double ttr =  Math.round(daysBetween * 10.0) / 10.0;

        dto.setTimeToRes(ttr);
        if(ticket.getAssignee() != null) {
            dto.setAssignee(ticket.getAssignee().getFirstName());
        }
        dto.setInProgress(ticket.getState() == TicketState.In_progress);
        return dto;
    }
}
