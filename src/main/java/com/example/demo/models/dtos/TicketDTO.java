package com.example.demo.models.dtos;

import com.example.demo.models.enums.TicketState;
import com.example.demo.models.enums.TicketUrgency;
import com.example.demo.models.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTO {
    private Integer id;
    private String name;
    private String category;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate createdOn;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate desiredResolutionDate;
    private String ownerEmail;
    @Enumerated(EnumType.STRING)
    private UserRole ownerRole;
    private String approverEmail;
    private String assigneeEmail;
    @Enumerated(EnumType.STRING)
    private TicketState state;
    @Enumerated(EnumType.STRING)
    private TicketUrgency urgency;
}







