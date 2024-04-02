package com.example.demo.models.dtos;

import java.time.LocalDateTime;

public record HistoryDTO (
        LocalDateTime timestamp,
        String action,
        String username,
        String description
){
}
