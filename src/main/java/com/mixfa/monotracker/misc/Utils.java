package com.mixfa.monotracker.misc;

import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@UtilityClass
public class Utils {
    public static final int DEFAULT_CURRENCY = 980;
    private static final LocalDate epochStart = LocalDate.of(1970, 1, 1);

    public static long currentMonthIndex() {
        return ChronoUnit.MONTHS.between(epochStart, LocalDate.now());
    }

    public static long currentWeekIndex() {
        return ChronoUnit.WEEKS.between(epochStart, LocalDate.now());
    }

    public static long currentYearIndex() {
        return ChronoUnit.YEARS.between(epochStart, LocalDate.now());
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
