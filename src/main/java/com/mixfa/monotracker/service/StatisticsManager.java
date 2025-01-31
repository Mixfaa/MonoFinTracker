package com.mixfa.monotracker.service;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.TxRecord;

public interface StatisticsManager {
    TxRecord addManualTransaction(TxRecord.ManualRegisterRequest request) throws AppException;

    void removeTransaction(String id) throws AppException;

    TxRecord updateTransaction(String id, TxRecord.UpdateRequest request) throws AppException;

    // TODO add methods to query stats
}
