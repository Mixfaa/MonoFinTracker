package com.mixfa.monotracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixfa.monotracker.model.MonoTx;
import com.mixfa.monotracker.service.MonoWebHook;
import com.mixfa.monotracker.service.feign.MonoApi;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
@Slf4j
public class MonoWebHookImpl implements MonoWebHook {
    private final List<Consumer<MonoTx>> subscribers = new CopyOnWriteArrayList<>();
    private final HttpServer httpServer;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final MonoApi.WebHookUrl webhookPayload;

    private final MonoApi monoApi;

    public MonoWebHookImpl(ObjectMapper objectMapper, MonoApi monoApi, @Value("${webhook.url}") String webHookUrl, @Value("${webhook.port}") int port) throws IOException {
        this.webhookPayload = new MonoApi.WebHookUrl(webHookUrl);

        this.monoApi = monoApi;
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", exchange -> {
            try {
                final var monoTx = objectMapper.readValue(exchange.getRequestBody(), MonoTx.class);

                executor.execute(() -> {
                    for (Consumer<MonoTx> subscriber : subscribers)
                        subscriber.accept(monoTx);
                });
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
            exchange.sendResponseHeaders(200, -1);
        });
        httpServer.start();
    }

    @Override
    public boolean install(String xToken) {
        monoApi.setupWebHook(xToken, webhookPayload);
        return true;
    }

    @Override
    public void subscribe(Consumer<MonoTx> consumer) {
        subscribers.add(consumer);
    }
}
