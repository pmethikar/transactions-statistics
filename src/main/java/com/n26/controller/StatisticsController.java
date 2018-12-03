package com.n26.controller;

import com.n26.model.Statistics;
import com.n26.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Statistics getStatistics() {
        return statisticsService.getStatistics();
    }
}
