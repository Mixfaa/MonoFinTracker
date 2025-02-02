package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.TxRecord;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TxRecordRepo {
    TxRecord save(TxRecord record, ObjectId userId);

    long delete(String id, ObjectId userId);

    Page<TxRecord> findAll(Pageable pageable, ObjectId userId);

    Page<TxRecord> findAllByTimestampBetween(long timestampStart, long timestampEnd, Pageable pageable, ObjectId userId);

    Optional<TxRecord> find(String id, ObjectId userId);

    Optional<TxRecord> findLast(ObjectId userId);


    String COLLECTION_POSTFIX = "_txRecords";

    static String makeCollectionName(ObjectId userId) {
        return userId.toHexString() + COLLECTION_POSTFIX;
    }
}