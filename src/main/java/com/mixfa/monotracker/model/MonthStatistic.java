package com.mixfa.monotracker.model;

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

@Document
@Getter
@With
@AllArgsConstructor
@FieldNameConstants
public class MonthStatistic {
    @Id
    private final ObjectId id;
    private final long monthStart;
    @DBRef
    private final User owner;
    private final int currencyCode;
    private final long delta;
    private final Map<String, Long> deltaByPurpose;

    public MonthStatistic(long monthStart, User owner, int currencyCode) {
        this.id = ObjectId.get();
        this.monthStart = monthStart;
        this.owner = owner;
        this.currencyCode = currencyCode;
        this.delta = 0;
        this.deltaByPurpose = new HashMap<>();
    }

    public MonthStatistic withTx(TxRecord txRecord, long convertedAmount) {
        return new MonthStatistic(id, monthStart, owner, currencyCode, delta + convertedAmount,
                new HashMap<>(deltaByPurpose) {{
                    compute(txRecord.description(), (_, delta) -> delta == null ? convertedAmount : delta + convertedAmount);
                }}); // видали как умею?
    }
}
