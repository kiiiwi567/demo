package com.example.demo.models.dtos;
import com.example.demo.models.enums.TicketUrgency;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.time.LocalDate;
@Data
public class TicketStatDTO {
    private String category;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate createdOn;
    private TicketUrgency urgency;
    private Double timeToRes;
    private boolean isInProgress;
    private String assignee;
}
