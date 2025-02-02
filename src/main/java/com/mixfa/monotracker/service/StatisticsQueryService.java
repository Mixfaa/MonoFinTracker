package com.mixfa.monotracker.service;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.PeriodStatistic;
import com.mixfa.monotracker.model.TxRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StatisticsQueryService {
    PeriodStatistic getStatisticByPeriod(long tsFrom, long tsTo) throws AppException;
    Page<TxRecord> getTxByPeriod(long tsFrom, long tsTo, Pageable pageable) throws AppException;
}
