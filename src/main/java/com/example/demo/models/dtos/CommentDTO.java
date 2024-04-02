package com.example.demo.models.dtos;

import java.time.LocalDateTime;

public record CommentDTO (
        String username,
        String text,
        LocalDateTime timestamp
){
}
