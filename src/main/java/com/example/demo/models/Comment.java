package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "comment", schema =  "public")
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "ticket_id")
    private Integer ticketId;
}
