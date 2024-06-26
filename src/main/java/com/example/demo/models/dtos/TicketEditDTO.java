package com.example.demo.models.dtos;

import com.example.demo.models.entities.Attachment;
import com.example.demo.models.enums.TicketUrgency;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate desiredResolutionDate;
    private List<AttachmentDTO> attachments;
}







