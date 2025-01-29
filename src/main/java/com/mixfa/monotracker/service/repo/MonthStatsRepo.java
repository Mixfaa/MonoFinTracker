package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.MonthStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthStatsRepo extends MongoRepository<MonthStatistic, String> {
}
