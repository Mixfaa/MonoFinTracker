package com.mixfa.monotracker.misc;

import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@UtilityClass
public class Utils {
    public static long getMonthStartTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        ZonedDateTime startOfPrevMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        return startOfPrevMonth.toEpochSecond();
    }

    public static void iterateUsers(UserService userService, Duration delay, Consumer<User> handler) {
        final var pageable = PageRequest.ofSize(20);
        final var usersAtomic = new AtomicReference<>(userService.listUsers(pageable));
        final var delayInMillis = delay.toMillis();
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            if (!usersAtomic.get().hasNext()) {
                executor.shutdown();
                return;
            }

            executor.scheduleAtFixedRate(() -> {
                for (User user : usersAtomic.get())
                    executor.scheduleAtFixedRate(() -> handler.accept(user), 0, delayInMillis, TimeUnit.MILLISECONDS);
            }, 0, delayInMillis, TimeUnit.MILLISECONDS);
        }
    }
}
