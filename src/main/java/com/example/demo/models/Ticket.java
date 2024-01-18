package com.example.demo.models;

import com.example.demo.models.enums.TicketState;
import com.example.demo.models.enums.TicketUrgency;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "ticket", schema =  "public")
public class Ticket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    private LocalDate createdOn;

    @Column(name = "desired_resolution_date")
    private LocalDate desiredResolutionDate;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", columnDefinition = "ticketState")
    private TicketState state;

    @Column(name = "category_id")
    private Integer categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", columnDefinition = "ticketUrgency")
    private TicketUrgency urgency;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;
}
