package com.mixfa.monotracker.service;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.PeriodStatistic;

public interface StatisticsQueryService {
    PeriodStatistic getByPeriod(long tsFrom, long tsTo) throws AppException;
}
