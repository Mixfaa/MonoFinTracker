package com.mixfa.monotracker.model;

import com.mixfa.monotracker.misc.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Document
@Getter
@With
@AllArgsConstructor
@FieldNameConstants
public class Statistic {
    @Id
    private final ObjectId id;
    private final long intervalValueIndex; // week / month / year index from epoch start
    private final Interval interval;
    @DBRef
    private final User owner;
    private final int currencyCode;
    private final long delta;
    @DBRef
    private final TxRecord[] unhandledRecords;
    private final Map<String, Long> deltaByPurpose;

    public Statistic(Interval interval, long intervalValueIndex, User owner, int currencyCode) {
        this.id = ObjectId.get();
        this.intervalValueIndex = intervalValueIndex;
        this.owner = owner;
        this.interval = interval;
        this.currencyCode = currencyCode;
        this.delta = 0;
        this.unhandledRecords = new TxRecord[0];
        this.deltaByPurpose = new HashMap<>();
    }

    public Statistic withTx(TxRecord txRecord, long convertedAmount) {
        return new Statistic(id, intervalValueIndex, interval, owner, currencyCode, delta + convertedAmount, new TxRecord[0],
                new HashMap<>(deltaByPurpose) {{
                    compute(txRecord.description(), (_, delta) -> delta == null ? convertedAmount : delta + convertedAmount);
                }}); // видали как умею?
    }

    @AllArgsConstructor
    public enum Interval {
        WEEK(Utils::currentWeekIndex),
        MONTH(Utils::currentMonthIndex),
        YEAR(Utils::currentYearIndex);

        private final Supplier<Long> currentIndexFunc;

        public long currentIndex() {
            return currentIndexFunc.get();
        }
    }
}
