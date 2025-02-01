package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.repo.TxRecordRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TxRecordRepoImpl implements TxRecordRepo {
    private final static String COLLECTION_POSTFIX = "_txRecords";
    private final MongoTemplate mongoTemplate;

    private final static Query EMPTY_QUERY = new Query();

    private static String makeCollectionName(ObjectId userId) {
        return userId.toHexString() + COLLECTION_POSTFIX;
    }

    @Override
    public TxRecord save(TxRecord record, ObjectId userId) {
        return mongoTemplate.save(record, makeCollectionName(userId));
    }

    @Override
    public long delete(String id, ObjectId userId) {
        var query = Query.query(
                Criteria.where(TxRecord.Fields.id).is(id)
        );
        return mongoTemplate.remove(query, makeCollectionName(userId)).getDeletedCount();
    }

    @Override
    @Transactional
    public Page<TxRecord> findAll(Pageable p, ObjectId userId) {
        var collectionName = makeCollectionName(userId);
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

    @Override
    public Page<TxRecord> findAllByTimestampBetween(long timestampStart, long timestampEnd, Pageable p, ObjectId userId) {
        var query = Query.query(
                Criteria.where(TxRecord.Fields.timestamp).gte(timestampStart).lte(timestampEnd)
        );
        var collectionName = makeCollectionName(userId);
        var total = mongoTemplate.count(query, collectionName);
        if (total == 0) return Page.empty(p);
        var list = mongoTemplate.find(query
                .limit(p.getPageSize())
                .skip(p.getOffset()), TxRecord.class, collectionName);

        return new PageImpl<>(list, p, total);
    }

    @Override
    public Optional<TxRecord> findLast(ObjectId userId) {
        var query = new Query().with(
                Sort.by(Sort.Order.desc(TxRecord.Fields.timestamp))
        );

        var collectionName = makeCollectionName(userId);

        return Optional.ofNullable(mongoTemplate.findOne(query, TxRecord.class, collectionName));
    }

    @Override
    public Optional<TxRecord> find(String id, ObjectId userId) {
        var query = Query.query(
                Criteria.where(TxRecord.Fields.id).is(id)
        );

        var collectionName = makeCollectionName(userId);
        return Optional.ofNullable(mongoTemplate.findOne(query, TxRecord.class, collectionName));
    }

}
