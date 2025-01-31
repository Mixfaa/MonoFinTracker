package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.misc.Exceptions;
import com.mixfa.monotracker.misc.Utils;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.MonoCurrencyConverter;
import com.mixfa.monotracker.service.StatisticsManager;
import com.mixfa.monotracker.service.UserService;
import com.mixfa.monotracker.service.repo.TxRecordRepo;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StatisticsManagerImpl implements StatisticsManager {
    private final UserService userService;
    private final TxRecordRepo txRecordRepo;
    private final MonoCurrencyConverter currencyConverter;

    @Override
    @Transactional
    public TxRecord addManualTransaction(TxRecord.ManualRegisterRequest request) throws AppException {
        var user = userService.authenticatedUser();
        var id = ObjectId.get().toHexString();
        var preferredCurrency = user.getPreferredCurrency();

        var amountInPreferredCurrency = request.amount();
        if (request.currencyCode() != preferredCurrency)
            amountInPreferredCurrency = currencyConverter.convert(request.amount(), request.currencyCode(), preferredCurrency);

        var tx = new TxRecord(
                id,
                preferredCurrency,
                amountInPreferredCurrency,
                request.mcc(),
                request.description(),
                request.timestamp(),
                user
        );
        return txRecordRepo.save(tx, user.getId());
    }

    @Override
    public void removeTransaction(String id) throws AppException {

    }

    @Override
    public TxRecord updateTransaction(String id, TxRecord.UpdateRequest request) throws AppException {
        var user = userService.authenticatedUser();
        var preferredCurrency = user.getPreferredCurrency();
        var amountInPreferredCurrency = request.amount();

        if (request.currencyCode() != preferredCurrency) {
            amountInPreferredCurrency = currencyConverter.convert(request.amount(), request.currencyCode(), preferredCurrency);
            request = request.withAmount(amountInPreferredCurrency).withCurrencyCode(preferredCurrency);
        }

        var tx = txRecordRepo.find(id, user.getId()).orElseThrow(() -> Exceptions.txNotFound(id));

        var newTx = Utils.merge(tx, request, TxRecord.class, TxRecord.UpdateRequest.class)
                .orElseThrow(() -> Exceptions.internalServerError(null));

        return txRecordRepo.save(newTx, user.getId());
    }
}
