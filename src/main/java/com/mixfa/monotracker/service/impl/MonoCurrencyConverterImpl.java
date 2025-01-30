package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.misc.Exceptions;
import com.mixfa.monotracker.service.MonoCurrencyConverter;
import com.mixfa.monotracker.service.feign.MonoApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MonoCurrencyConverterImpl implements MonoCurrencyConverter {
    private final MonoApi monoApi;


    @Override
    public long convert(long amount, int currencyCodeA, int currencyCodeB) throws AppException {
        if (currencyCodeA == currencyCodeB) return amount;
        var currencyRates = monoApi.getCurrencies();
        var rate = Arrays.stream(currencyRates)
                .filter(r ->
                        r.currencyCodeA() == currencyCodeA && r.currencyCodeB() == currencyCodeB)
                .findFirst()
                .orElse(
                        Arrays.stream(currencyRates)
                                .filter(r ->
                                        r.currencyCodeA() == currencyCodeB && r.currencyCodeB() == currencyCodeA)
                                .findFirst().orElseThrow(() -> Exceptions.cantConvertCurrency(currencyCodeA, currencyCodeB))
                );

        return (long) (rate.currencyCodeA() == currencyCodeA ? amount * rate.rateCross() : amount * (1.0 / rate.rateCross()));
    }
}
