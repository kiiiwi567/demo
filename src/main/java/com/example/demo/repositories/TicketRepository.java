package com.example.demo.repositories;

import com.example.demo.models.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface TicketRepository extends JpaRepository <Ticket, Integer> {

    @Query("select t from Ticket t " +
            "left join fetch t.owner o left join fetch t.approver left join fetch t.assignee " +
            "where o.email = :employeeEmail " +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForEmployee(@Param("employeeEmail") String employeeEmail);

    @Query("SELECT t FROM Ticket t " +
            "LEFT JOIN fetch t.approver a LEFT JOIN fetch t.owner o left join fetch t.assignee " +
            "WHERE (o.email = :managerEmail) " +
            "OR ((o.role = 'Employee') AND (t.state = 'New')) " +
            "OR ((a.email = :managerEmail) AND (t.state IN ('Approved', 'Declined', 'Cancelled', 'In_progress', 'Done')))" +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForManager(@Param("managerEmail") String managerEmail);

    @Query("SELECT t FROM Ticket t " +
            "LEFT JOIN fetch t.assignee a LEFT JOIN fetch t.owner o LEFT JOIN fetch t.approver " +
            "WHERE ((o.role IN ('Employee', 'Manager')) AND (t.state = 'Approved')) " +
            "OR ((a.email = :engineerEmail) AND (t.state in ('In_progress', 'Done'))) " +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForEngineer(@Param("engineerEmail") String engineerEmail);

    @Query("SELECT t FROM Ticket t WHERE t.id = :ticketId")
    Ticket getTicketForOverviewById(@Param ("ticketId") Integer ticketId);

    @Query("SELECT t FROM Ticket t")
    List<Ticket> getAllTickets();
}

