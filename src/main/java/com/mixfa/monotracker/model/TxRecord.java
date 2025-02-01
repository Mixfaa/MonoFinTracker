package com.mixfa.monotracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
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
    private final long timestamp;
    @DBRef
    private final User owner;

    public record ManualRegisterRequest(
            int currencyCode,
            long amount,
            int mcc,
            String description,
            long timestamp
    ) {
    }

    @With
    public record UpdateRequest(
            int currencyCode,
            long amount,
            int mcc,
            String description,
            long timestamp
    ) {
    }
}
 