package com.mixfa.monotracker.service;

public interface MonoCurrencyConverter {
    long convert(long amount, int currencyCodeA, int currencyCodeB) throws Exception;
}
