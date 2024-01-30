package com.example.demo.models;

import com.example.demo.models.enums.TicketState;
import com.example.demo.models.enums.TicketUrgency;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "ticket", schema =  "public")
public class Ticket {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "ticket_id_seq", sequenceName = "ticket_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_id_seq")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    private LocalDate createdOn = LocalDate.now();

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
    @Type(PostgreSQLEnumType.class)
    private TicketState state = TicketState.New;

    @Column(name = "category_id")
    private Integer categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", columnDefinition = "ticketUrgency")
    @Type(PostgreSQLEnumType.class)
    private TicketUrgency urgency;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;
}