package com.example.demo.models.dtos;

import com.example.demo.models.entities.Ticket;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TicketFullDTOMapper extends TicketDTOMapper {
    @Override
    public TicketFullDTO apply(Ticket ticket) {
        TicketDTO shortDTO = super.apply(ticket);
        TicketFullDTO fullDTO = new TicketFullDTO();
        BeanUtils.copyProperties(shortDTO,fullDTO);
        fullDTO.setDescription(ticket.getDescription());
        fullDTO.setOwnerUsername(ticket.getOwner().getFirstName());

        if(ticket.getApprover() != null) {
            fullDTO.setApproverUsername(ticket.getApprover().getFirstName());
        }
        if(ticket.getAssignee() != null) {
            fullDTO.setAssigneeUsername(ticket.getAssignee().getFirstName());
        }

        fullDTO.setHistoryRecords(ticket.getHistoryRecords().stream()
                .map(hr -> new HistoryDTO(hr.getTimestamp(),
                                        hr.getAction(),hr.getUser().getFirstName(),
                                        hr.getDescription()))
                .toList());

        if (ticket.getFeedback() != null) {
            fullDTO.setFeedbacks(ticket.getFeedback().stream()
                    .map(f -> new FeedbackDTO(f.getRate(), f.getTimestamp()))
                    .toList());
        }
        if(ticket.getAttachments().size() != 0){
            fullDTO.setAttachments(ticket.getAttachments());
        }
        if(ticket.getComments() != null) {
            fullDTO.setComments(ticket.getComments().stream()
                    .map(c -> new CommentDTO(c.getUser().getFirstName(), c.getText(),
                            c.getTimestamp()))
                    .toList());
        }

        return fullDTO;
    }
}
