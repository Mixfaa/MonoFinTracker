package com.mixfa.monotracker.model;

import lombok.experimental.FieldNameConstants;

import java.util.Map;

@FieldNameConstants
public record PeriodStatistic(
        long tsFrom,
        long tsTo,
        int currencyCode,
        long delta,
        Map<String, Long> deltaToDesc
) {
}
