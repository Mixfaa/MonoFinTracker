package com.mixfa.monotracker.controller;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.PeriodStatistic;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.StatisticsManager;
import com.mixfa.monotracker.service.StatisticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class StatisticsController {
    private final StatisticsManager statisticsManager;
    private final StatisticsQueryService statisticsQueryService;

    @PostMapping("/tx")
    public TxRecord addManualTx(@RequestBody TxRecord.ManualRegisterRequest request) throws AppException {
        return statisticsManager.addManualTransaction(request);
    }


    @DeleteMapping("/tx/{id}")
    public void deleteTx(@PathVariable String id) throws AppException {
        statisticsManager.removeTransaction(id);
    }

    @PatchMapping("/tx/{id}")
    public TxRecord updateTx(@PathVariable String id, @RequestBody TxRecord.UpdateRequest request) throws AppException {
        return statisticsManager.updateTransaction(id, request);
    }

    @GetMapping("/period/{from}/{to}")
    public PeriodStatistic getByPeriod(@PathVariable("from") long from, @PathVariable("to") long to) throws AppException {
        return statisticsQueryService.getByPeriod(from, to);
    }


}
