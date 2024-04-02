package com.example.demo.models.dtos;

import com.example.demo.models.entities.Attachment;
import com.example.demo.models.enums.TicketUrgency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketEditDTO {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private TicketUrgency urgency;
    private LocalDate desiredResolutionDate;
    private List<Attachment> attachments;
}







