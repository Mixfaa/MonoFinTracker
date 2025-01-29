package com.mixfa.monotracker.service;

import com.mixfa.monotracker.model.MonoTx;

import java.util.function.Consumer;

public interface MonoWebHook {
    boolean install(String xToken);

    void subscribe(Consumer<MonoTx> consumer);
}
