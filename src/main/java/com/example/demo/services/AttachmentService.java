package com.example.demo.services;

import com.example.demo.models.Attachment;
import com.example.demo.repositories.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    public Attachment getAttachmentByName(String name) {
        return attachmentRepository.getAttachmentByName(name);
    }
}
