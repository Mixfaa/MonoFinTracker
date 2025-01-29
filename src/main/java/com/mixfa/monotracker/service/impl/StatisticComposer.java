package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.Utils;
import com.mixfa.monotracker.model.MonthStatistic;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.MonoCurrencyConverter;
import com.mixfa.monotracker.service.repo.MonthStatsRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticComposer {
    private final static String CRON_MONTH_MIDNIGHT = "5 0 1 * *"; // + 5min
    private final static String CRON_YEAR_MIDNIGHT = "5 0 1 1 *"; // +5 min

    private final MonoTxHandler monoTxHandler;
    private final MonoCurrencyConverter monoCurrencyConverter;
    private final MongoTemplate mongoTemplate;
    private final MonthStatsRepo monthStatsRepo;

    @PostConstruct
    public void init() {
        monoTxHandler.subscribeForTxRecords(this::addToMonthStatistic);
    }

    @SneakyThrows
    private void addToMonthStatistic(TxRecord txRecord) {
        Sort sort = Sort.by(Sort.Order.desc(MonthStatistic.Fields.monthStart));
        var stat = mongoTemplate.findOne(
                Query.query(
                        Criteria.where(MonthStatistic.Fields.owner).is(txRecord.owner())
                ).with(sort),
                MonthStatistic.class
        );

        if (stat == null) // we should create new one;
            stat = new MonthStatistic(Utils.getMonthStartTime(), txRecord.owner(), txRecord.owner().getPreferredCurrency());


        System.out.println("I found: " + stat);

        var convertedAmount = monoCurrencyConverter.convert(txRecord.amount(), txRecord.currencyCode(), stat.getCurrencyCode());

        monthStatsRepo.save(stat.withTx(txRecord, convertedAmount));
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
