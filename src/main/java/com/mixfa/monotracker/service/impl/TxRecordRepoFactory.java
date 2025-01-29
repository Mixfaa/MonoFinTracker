package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.service.repo.TxRecordRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TxRecordRepoFactory {
    private final MongoTemplate mongoTemplate;
    private final Map<String, TxRecordRepo> cache = new ConcurrentHashMap<>();
    
    public TxRecordRepo get(ObjectId userId) {
        return get(userId.toHexString());
    }

    public TxRecordRepo get(String userId) {
        return cache.putIfAbsent(userId, new TxRecordRepoImpl(userId, mongoTemplate));
    }
}
