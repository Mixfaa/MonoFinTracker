package com.mixfa.monotracker.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "MonoAPI", url = "${monoendpoint.url}")
public interface MonoApi {

    @GetMapping("/personal/client-info")
    ClientInfo getClientInfo(@RequestHeader("X-Token") String xToken);

    @PostMapping("/personal/webhook")
    void setupWebHook(@RequestHeader("X-Token") String xToken, @RequestBody WebHookUrl webHookUrl);

    record WebHookUrl(
            String webHookUrl
    ) {
    }

    record AccountInfo(
            String id,
            String sendId,
            long balance,
            long creditLimit,
            String type,
            int currencyCode,
            String cashbackType,
            String[] maskedPan,
            String iban
    ) {
    }

    record JarInfo(
            String id,
            String sendId,
            String title,
            String description,
            int currencyCode,
            long balance,
            long goal
    ) {
    }

    record ClientInfo(
            String clientId,
            String name,
            String webHookUrl,
            String permissions,
            AccountInfo[] accounts,
            JarInfo[] jars
    ) {
    }
}
