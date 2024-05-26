package com.example.demo.models.dtos;

import com.example.demo.models.enums.TicketUrgency;
import com.example.demo.models.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketCreateDTO {
    private Integer category;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private TicketUrgency urgency;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate desiredResolutionDate;
    private String comment;
    private String ownerEmail;
    @Enumerated(EnumType.STRING)
    private UserRole ownerRole;
}
