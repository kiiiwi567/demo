package com.example.demo.models.dtos;

import com.example.demo.models.entities.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketFullDTO extends TicketDTO {
    private String description;
    private String ownerUsername;
    private String assigneeUsername;
    private String approverUsername;
    private List<FeedbackDTO> feedbacks;
    private List<Attachment> attachments;
    private List<CommentDTO> comments;
    private List<HistoryDTO> historyRecords;
}
