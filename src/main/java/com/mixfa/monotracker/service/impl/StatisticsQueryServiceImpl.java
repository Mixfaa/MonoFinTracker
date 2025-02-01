package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.misc.Exceptions;
import com.mixfa.monotracker.model.PeriodStatistic;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.StatisticsQueryService;
import com.mixfa.monotracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.mixfa.monotracker.service.repo.TxRecordRepo.makeCollectionName;

@Service
@RequiredArgsConstructor
public class StatisticsQueryServiceImpl implements StatisticsQueryService {
    //    private final TxRecordRepo txRecordRepo;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;

    @Override
    public PeriodStatistic getByPeriod(long tsFrom, long tsTo) throws AppException {
        record DeltaRes(
                Object _id,
                long delta
        ) {
        }

        record DeltaNodeRes(
                String description,
                long delta
        ) {
        }

        var user = userService.authenticatedUser();
        var currencyCode = user.getPreferredCurrency();
        var collectionName = makeCollectionName(user.getId());

        var selectAggregation = Aggregation.match(
                Criteria.where(TxRecord.Fields.timestamp).gte(tsFrom).lte(tsTo)
        );

        var deltaAggregation = Aggregation.group()
                .sum(TxRecord.Fields.amount)
                .as(PeriodStatistic.Fields.delta);

        var deltaResult = mongoTemplate.aggregate(
                Aggregation.newAggregation(selectAggregation, deltaAggregation),
                collectionName,
                DeltaRes.class
        ).getUniqueMappedResult();

        if (deltaResult == null)
            throw Exceptions.internalServerError(new Throwable("deltaResult is null"));

        var splitDeltaAggr = Aggregation.group(TxRecord.Fields.description)
                .sum(TxRecord.Fields.amount).as("delta");

        var renameAggr = Aggregation.project("delta", "_id").and("_id").as(TxRecord.Fields.description);

        var splitDeltaResult = mongoTemplate.aggregate(
                Aggregation.newAggregation(selectAggregation, splitDeltaAggr, renameAggr),
                collectionName,
                DeltaNodeRes.class
        ).getMappedResults();

        return new PeriodStatistic(
                tsFrom,
                tsTo,
                currencyCode,
                deltaResult.delta,
                splitDeltaResult
                        .stream()
                        .collect(Collectors.toMap(DeltaNodeRes::description, DeltaNodeRes::delta))
        );
    }
}
