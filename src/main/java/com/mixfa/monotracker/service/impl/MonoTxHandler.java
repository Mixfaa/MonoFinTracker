package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.MccCodeTable;
import com.mixfa.monotracker.model.MonoTx;
import com.mixfa.monotracker.model.TxRecord;
import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.MonoWebHook;
import com.mixfa.monotracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class MonoTxHandler implements ApplicationListener<UserService.UserRegisterEvent> {
    private final MonoWebHook monoWebHook;
    private final TxRecordRepoFactory txRecordRepoFactory;
    private final UserService userService;

    public MonoTxHandler(MonoWebHook monoWebHook, UserService userService, TxRecordRepoFactory txRecordRepoFactory) {
        this.monoWebHook = monoWebHook;
        this.userService = userService;
        this.txRecordRepoFactory = txRecordRepoFactory;

        monoWebHook.subscribe(this::handleMonoTx);

        try (var executor = Executors.newScheduledThreadPool(1)) {
            final var pageable = PageRequest.ofSize(20);
            final AtomicReference<Page<User>> pageAtomic = new AtomicReference<>(userService.listUsers(pageable));

            executor.scheduleAtFixedRate(() -> {
                if (!pageAtomic.get().hasNext()) {
                    executor.shutdown();
                    return;
                }

                for (User user : pageAtomic.get())
                    monoWebHook.install(user.getXToken());

                pageAtomic.set(userService.listUsers(pageable));
            }, 0, 5, TimeUnit.SECONDS);
        }
    }

    private void handleMonoTx(MonoTx monoTx) {
        var accountId = monoTx.data().account();
        var user = userService.findByMonoAccount(accountId).orElseThrow();

        var statementItem = monoTx.data().statementItem();
        var txRecord = new TxRecord(
                statementItem.id(),
                statementItem.currencyCode(),
                statementItem.amount(),
                statementItem.mcc(),
                MccCodeTable.getDescription(statementItem.mcc()),
                statementItem.balance(),
                statementItem.time()
        );

        txRecordRepoFactory.get(user.getId()).save(txRecord);
    }

    @Override
    public void onApplicationEvent(UserService.UserRegisterEvent event) {
        monoWebHook.install(event.user().getXToken());
    }
}
