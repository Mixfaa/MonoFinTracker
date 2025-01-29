package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.TxRecord;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TxRecordRepo {
    void save(TxRecord record, ObjectId userId);

    Page<TxRecord> findAll(Pageable pageable, ObjectId userId);

    Page<TxRecord> findAllByTimestampBetween(long timestampStart, long timestampEnd, Pageable pageable, ObjectId userId);
}