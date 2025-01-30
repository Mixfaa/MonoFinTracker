package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.ArrayUtils;
import com.mixfa.monotracker.misc.Utils;
import com.mixfa.monotracker.model.MonthStatistic;
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

    private void addToMonthStatistic(TxRecord txRecord) {
        final var owner = txRecord.owner();
        final var preferredCurrency = owner.getPreferredCurrency();

        Sort sort = Sort.by(Sort.Order.desc(MonthStatistic.Fields.monthStart));
        var query = Query.query(Criteria.where(MonthStatistic.Fields.owner).is(owner)).with(sort);
        var stat = mongoTemplate.findOne(query, MonthStatistic.class);

        if (stat == null) // we should create new one;
            stat = new MonthStatistic(Utils.getMonthStartTime(), owner, preferredCurrency);
        else {
            final var currentMonthStart = Utils.getMonthStartTime();
            if (stat.getMonthStart() != currentMonthStart)
                stat = new MonthStatistic(currentMonthStart, owner, preferredCurrency);
        }
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
        addToMonthStatistic(event.newRecord());
    }

//    @Scheduled(cron = CRON_MONTH_MIDNIGHT)
//    public void composeMonthStatistic() {
//        Utils.iterateUsers(userService, Duration.ofSeconds(15), user -> {
//            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
//
//            ZonedDateTime startOfPrevMonth = now.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
//            long startTimestamp = startOfPrevMonth.toEpochSecond();
//            long endTimestamp = startOfPrevMonth.plusMonths(1).minusSeconds(1).toEpochSecond();
//
//            final var pageable = PageRequest.ofSize(20);
//
//            final Supplier<Page<TxRecord>> fetchRecordsFunc = () -> txRecordRepo.findAllByTimestampBetween(
//                    startTimestamp,
//                    endTimestamp,
//                    pageable,
//                    user.getId()
//            );
//
//
//            var page = fetchRecordsFunc.get();
//
//            var targetCurrency = user.getPreferredCurrency();
//
//            do {
//                for (TxRecord txRecord : page) {
//
//                }
//                page = fetchRecordsFunc.get();
//            } while (page.hasNext());
//
//        });
//    }
//
//    @Scheduled(cron = CRON_YEAR_MIDNIGHT)
//    public void composeYearStatistic() {
//    }
}
