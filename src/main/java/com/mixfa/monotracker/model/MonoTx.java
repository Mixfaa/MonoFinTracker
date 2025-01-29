package com.mixfa.monotracker.model;

public record MonoTx(
        String type,
        Data data
) {

    public record Data(
            String account,
            StatementItem statementItem
    ) {

    }

    public record StatementItem(
            String id,
            long time,
            String description,
            int mcc,
            int originalMcc,
            long amount,
            long operationAmount,
            int currencyCode,
            long commissionRate,
            long cashbackAmount,
            long balance,
            boolean hold,
            String receiptId
    ) {
    }
}
