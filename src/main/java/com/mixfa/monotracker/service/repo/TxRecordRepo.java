package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.TxRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxRecordRepo extends MongoRepository<TxRecord, String> {
}
