package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "attachment", schema =  "public")
public class Attachment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "contents")
    private String contents;

    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "name")
    private String name;
}
