package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.ArrayUtils;
import com.mixfa.monotracker.model.Statistic;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.MonoCurrencyConverter;
import com.mixfa.monotracker.service.repo.MonthStatsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticComposer implements ApplicationListener<TxRecord.OnNewRecordEvent> {

    private final MonoCurrencyConverter monoCurrencyConverter;
    private final MonthStatsRepo monthStatsRepo;
    private final MongoTemplate mongoTemplate;

    private void addToStatistic(TxRecord txRecord, Statistic.Interval interval) {
        final var currentIndex = interval.currentIndex();
        final var owner = txRecord.owner();
        final var preferredCurrency = owner.getPreferredCurrency();

        Sort sort = Sort.by(Sort.Order.desc(Statistic.Fields.intervalValueIndex));
        var query = Query.query(
                Criteria.where(Statistic.Fields.owner).is(owner)
                        .and(Statistic.Fields.interval).is(interval)
        ).with(sort);
        var stat = mongoTemplate.findOne(query, Statistic.class);

        if (stat == null || stat.getIntervalValueIndex() != currentIndex) // we should create new one;
            stat = new Statistic(interval, currentIndex, owner, preferredCurrency);
        
        try {
            var convertedAmount = monoCurrencyConverter.convert(txRecord.amount(), txRecord.currencyCode(), stat.getCurrencyCode());
            monthStatsRepo.save(stat.withTx(txRecord, convertedAmount));
        } catch (Exception e) {
            monthStatsRepo.save(stat.withUnhandledRecords(ArrayUtils.add(stat.getUnhandledRecords(), TxRecord[]::new, txRecord)));
            log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void onApplicationEvent(TxRecord.OnNewRecordEvent event) {
        addToStatistic(event.newRecord(), Statistic.Interval.YEAR);
        addToStatistic(event.newRecord(), Statistic.Interval.MONTH);
    }
}
