package com.example.demo.services;

import com.example.demo.models.dtos.AttachmentDTO;
import com.example.demo.models.entities.Attachment;
import com.example.demo.models.entities.History;
import com.example.demo.models.entities.User;
import com.example.demo.repositories.AttachmentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    @PersistenceContext
    private EntityManager entityManager;
    public Attachment getAttachmentByNameAndTicketId(String name, Integer ticketId) {
        return attachmentRepository.getAttachmentByNameAndTicketId(name, ticketId);
    }

    public List<Attachment> processAttachmentsForEdition(MultipartFile[] newAtt,
                                                         Integer ticketId,
                                                         List<Attachment> oldAtt,
                                                         List<AttachmentDTO> attToDel,
                                                         User editingUser){
        List<Attachment> finAtt = new ArrayList<>();
        if(newAtt!=null) {
            finAtt.addAll(addAttachmentsToList(newAtt, ticketId));
            attachmentRepository.saveAll(finAtt);
        }

        finAtt.addAll(oldAtt);

        if(attToDel!= null){
            List<Integer> idsToRemove = getAttIdsForRemoval(attToDel, ticketId, editingUser);
            finAtt.removeIf(a -> idsToRemove.contains(a.getId()));
        }
        return finAtt;
    }

    public List<Attachment> addAttachmentsToList(MultipartFile[] attachmentFiles, Integer ticketId){
        List<Attachment> attachmentList = new ArrayList<>();
        for (MultipartFile attachmentFile : attachmentFiles) {
            attachmentList.add(MultipartToAttachment(attachmentFile, ticketId));
        }
        return attachmentList;
    }

    public Attachment MultipartToAttachment(MultipartFile file, Integer ticketId){
        Attachment attachment = new Attachment();
        try {
            attachment.setContents(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        attachment.setTicketId(ticketId);
        attachment.setName(file.getOriginalFilename());
        return attachment;
    }


    public List<Integer> getAttIdsForRemoval(List<AttachmentDTO> attToDel, Integer ticketID, User currentUser){
        List<Integer> idsToDelete= new ArrayList<>();
        for (AttachmentDTO att : attToDel) {
            attachmentRepository.deleteAttachmentById(att.id());
            idsToDelete.add(att.id());
            History attRecord = new History(ticketID,
                    "File removed",
                    currentUser,
                    "File removed: " + att.fileName());
            entityManager.persist(attRecord);
        }
        return idsToDelete;
    }

    public void addAttachmentsToNewTicket(MultipartFile[] ticketFiles, Integer ticketId, User ticketCreator){
        List<Attachment> attachments = addAttachmentsToList(ticketFiles, ticketId);
        for (Attachment attachment : attachments){
            entityManager.persist(attachment);
            History attRecord = new History(ticketId,
                    "File is attached",
                    ticketCreator,
                    "File is attached: " + attachment.getName());
            entityManager.persist(attRecord);
        }
    }
}
