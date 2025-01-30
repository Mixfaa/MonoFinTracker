package com.mixfa.monotracker.service;

import com.mixfa.monotracker.misc.AppException;

public interface MonoCurrencyConverter {
    long convert(long amount, int currencyCodeA, int currencyCodeB) throws AppException;
}
