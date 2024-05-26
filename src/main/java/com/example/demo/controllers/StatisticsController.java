package com.example.demo.controllers;

import com.example.demo.models.dtos.StatDTO;
import com.example.demo.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/getStatistics")
    public ResponseEntity<StatDTO> getStatistics(){
        return ResponseEntity.ok().body(statisticsService.computeTotalStats());
    }
}
