package com.example.demo.repositories;

import com.example.demo.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository <Ticket, Integer> {
    @Query(nativeQuery = true, value = "select * from ticket")
    List<Ticket> getAllTickets();

    @Query("select t from Ticket t where t.owner.email = :employeeEmail " +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForEmployee(@Param("employeeEmail") String employeeEmail);

    @Query("SELECT t FROM Ticket t " +
            "LEFT JOIN t.approver a LEFT JOIN t.owner o " +
            "WHERE (t.owner.email = :managerEmail) " +
            "OR ((t.owner.role = 'Employee') AND (t.state = 'New')) " +
            "OR ((t.approver.email = :managerEmail) AND (t.state IN ('Approved', 'Declined', 'Cancelled', 'In_progress', 'Done')))" +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForManager(@Param("managerEmail") String managerEmail);

    @Query("SELECT t FROM Ticket t " +
            "LEFT JOIN t.assignee a LEFT JOIN t.owner o " +
            "WHERE ((o.role IN ('Employee', 'Manager')) AND (t.state = 'Approved')) " +
            "OR ((a.email = :engineerEmail) AND (t.state in ('In_progress', 'Done'))) " +
            "ORDER BY t.urgency, t.desiredResolutionDate")
    List<Ticket> getAllTicketsForEngineer(@Param("engineerEmail") String engineerEmail);

    @Query("SELECT t FROM Ticket t WHERE t.id = :ticketId")
    Ticket getTicketForOverviewById(@Param ("ticketId") Integer ticketId);
}
