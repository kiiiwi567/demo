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
}


//    @Query("select new com.example.demo.models.dtos.TicketDTO(t.id, t.name, t.createdOn, t.desiredResolutionDate," +
//            "o.email, o.role, ap.email, asg.email, t.state, t.urgency) " +
//            "from Ticket t " +
//            "LEFT JOIN t.approver ap LEFT JOIN t.owner o LEFT JOIN t.assignee asg " +
//            "where t.owner.email = :employeeEmail " +
//            "ORDER BY t.urgency, t.desiredResolutionDate")
//    List<TicketDTO> getAllTicketDTOsForEmployee(@Param("employeeEmail") String employeeEmail);

    //    @Query("SELECT new com.example.demo.models.dtos.TicketDTO(t.id, t.name, t.createdOn, t.desiredResolutionDate," +
//            "o.email, o.role, ap.email, asg.email, t.state, t.urgency) " +
//            "FROM Ticket t " +
//            "LEFT JOIN t.approver ap LEFT JOIN t.owner o LEFT JOIN t.assignee asg " +
//            "WHERE (o.email = :managerEmail) " +
//            "OR ((o.role = 'Employee') AND (t.state = 'New')) " +
//            "OR ((ap.email = :managerEmail) AND (t.state IN ('Approved', 'Declined', 'Cancelled', 'In_progress', 'Done')))" +
//            "ORDER BY t.urgency, t.desiredResolutionDate")
//    List<TicketDTO> getAllTicketDTOsForManager(@Param("managerEmail") String managerEmail);

//    @Query("SELECT new com.example.demo.models.dtos.TicketDTO(t.id, t.name, t.createdOn, t.desiredResolutionDate," +
//            "o.email, o.role, ap.email, asg.email, t.state, t.urgency) " +
//            "FROM Ticket t " +
//            "LEFT JOIN t.approver ap LEFT JOIN t.owner o LEFT JOIN t.assignee asg " +
//            "WHERE ((o.role IN ('Employee', 'Manager')) AND (t.state = 'Approved')) " +
//            "OR ((asg.email = :engineerEmail) AND (t.state in ('In_progress', 'Done'))) " +
//            "ORDER BY t.urgency, t.desiredResolutionDate")
//    List<TicketDTO> getAllTicketDTOsForEngineer(@Param("engineerEmail") String engineerEmail);
