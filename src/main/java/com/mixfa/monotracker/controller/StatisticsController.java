package com.mixfa.monotracker.controller;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.PeriodStatistic;
import com.mixfa.monotracker.service.StatisticsManager;
import com.mixfa.monotracker.service.StatisticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class StatisticsController {
    private final StatisticsManager statisticsManager;
    private final StatisticsQueryService statisticsQueryService;


    @GetMapping("/period/{from}/{to}")
    public PeriodStatistic getByPeriod(@PathVariable("from") long from, @PathVariable("to") long to) throws AppException {
        return statisticsQueryService.getByPeriod(from, to);
    }


}
