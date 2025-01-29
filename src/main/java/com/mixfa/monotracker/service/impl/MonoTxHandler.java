package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.MccCodeTable;
import com.mixfa.monotracker.misc.Utils;
import com.mixfa.monotracker.model.MonoTx;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.service.MonoWebHook;
import com.mixfa.monotracker.service.UserService;
import com.mixfa.monotracker.service.repo.TxRecordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Service
public class MonoTxHandler implements ApplicationListener<UserService.UserRegisterEvent> {
    private final MonoWebHook monoWebHook;
    private final TxRecordRepo txRecordRepo;
    private final UserService userService;
    private final List<Consumer<TxRecord>> txRecordHandlers = new CopyOnWriteArrayList<>();

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public MonoTxHandler(MonoWebHook monoWebHook, UserService userService, TxRecordRepo txRecordRepo) {
        this.monoWebHook = monoWebHook;
        this.userService = userService;
        this.txRecordRepo = txRecordRepo;

        monoWebHook.subscribe(this::handleMonoTx);

        Utils.iterateUsers(userService, Duration.ofSeconds(5), user -> monoWebHook.install(user.getXToken()));
    }

    private void handleMonoTx(MonoTx monoTx) {
        var accountId = monoTx.data().account();
        var user = userService.findByMonoAccount(accountId).orElseThrow();

        var statementItem = monoTx.data().statementItem();
        final var txRecord = new TxRecord(
                statementItem.id(),
                statementItem.currencyCode(),
                statementItem.amount(),
                statementItem.mcc(),
                MccCodeTable.getDescription(statementItem.mcc()),
                statementItem.balance(),
                statementItem.time()
        );

        txRecordRepo.save(txRecord, user.getId());

        for (Consumer<TxRecord> handler : txRecordHandlers) {
            executor.execute(() -> {
                try {
                    handler.accept(txRecord);
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                }
            });
        }
    }

    public void subscribeForTxRecords(Consumer<TxRecord> handler) {
        txRecordHandlers.add(handler);
    }

    @Override
    public void onApplicationEvent(UserService.UserRegisterEvent event) {
        monoWebHook.install(event.user().getXToken());
    }
}
