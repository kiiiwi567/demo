package com.example.demo.models.dtos;

import com.example.demo.models.enums.PeriodEnum;

public record ProblemsDTO(
        String firstMetricLeader,
        String secondMetricLeader,
        String thirdMetricLeader,
        String allMetricLeader,
        PeriodEnum period
) {

}
