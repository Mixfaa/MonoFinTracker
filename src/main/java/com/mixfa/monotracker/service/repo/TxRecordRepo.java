package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.TxRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TxRecordRepo {
    void save(TxRecord record);

    Page<TxRecord> findAll(Pageable pageable);
}