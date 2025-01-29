package com.mixfa.monotracker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class TxRecord {
    @Id
    private final String id; // from MonoTx
    private final int currencyCode;
    private final long amount;
    private final int mcc;
    private final String description;
    private final long balance;
    private final long time;
    @DBRef
    private final User user;
}
