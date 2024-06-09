package com.example.demo.models.dtos.mappings;

import com.example.demo.models.dtos.AttachmentDTO;
import com.example.demo.models.dtos.TicketEditDTO;
import com.example.demo.models.entities.Attachment;
import com.example.demo.models.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
            List<AttachmentDTO> attDTOs = new ArrayList<>();
            for(Attachment att: ticket.getAttachments()){
                attDTOs.add(new AttachmentDTO(att.getId(), att.getName()));
            }
            dto.setAttachments(attDTOs);
        }
        return dto;
    }
}
