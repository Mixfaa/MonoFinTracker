package com.mixfa.monotracker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@FieldNameConstants
public class TxRecord { // uses many collections (and collection name for identifying user (userid_txRecords))
    @Id
    private final String id; // from MonoTx
    private final int currencyCode;
    private final long amount;
    private final int mcc;
    private final String description;
    private final long balance;
    private final long timestamp;
    @DBRef
    private final User owner;

    @Getter
    @Accessors(fluent = true)
    public static class OnNewRecordEvent extends ApplicationEvent {
        private final TxRecord newRecord;

        public OnNewRecordEvent(TxRecord txRecord, Object source) {
            super(source);
            this.newRecord = txRecord;
        }
    }
}
 