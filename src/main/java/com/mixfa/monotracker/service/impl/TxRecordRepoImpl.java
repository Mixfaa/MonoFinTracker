package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.repo.TxRecordRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

public class TxRecordRepoImpl implements TxRecordRepo {
    private final static String COLLECTION_POSTFIX = "_txRecords";
    private final String collectionName;
    private final MongoTemplate mongoTemplate;

    private final static Query EMPTY_QUERY = new Query();

    TxRecordRepoImpl(String collectionPrefix, MongoTemplate mongoTemplate) {
        this.collectionName = collectionPrefix + COLLECTION_POSTFIX;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(TxRecord record) {
        mongoTemplate.save(record, collectionName);
    }

    @Override
    @Transactional
    public Page<TxRecord> findAll(Pageable p) {
        var list = mongoTemplate.find(
                new Query()
                        .limit(p.getPageSize())
                        .skip(p.getOffset()),
                TxRecord.class,
                collectionName
        );
        var count = mongoTemplate.count(EMPTY_QUERY, collectionName);
        return new PageImpl<>(list, p, count);
    }
}
