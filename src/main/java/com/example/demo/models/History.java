package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "history", schema =  "public")
public class History {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "action")
    private String action;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "description")
    private String description;
}
