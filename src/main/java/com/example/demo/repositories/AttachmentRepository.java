package com.example.demo.repositories;

import com.example.demo.models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    @Query("select a from Attachment a where a.name = :name and a.ticketId = :ticketId")
    Attachment getAttachmentByNameAndTicketId(@Param("name") String name,
                                              @Param("ticketId") Integer ticketId);
}
