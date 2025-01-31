package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthStatsRepo extends MongoRepository<Statistic, String> {
}
