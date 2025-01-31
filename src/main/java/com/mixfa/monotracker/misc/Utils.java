package com.mixfa.monotracker.misc;

import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
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

    /**
     * Creates new Object of type ResultT from fields of entity and nonnull fields of mergeObj
     */
    public static <ResultT, MergeT> Optional<ResultT> merge(ResultT entity, MergeT mergeObj, Class<ResultT> resClass, Class<MergeT> mergeClass) {
        try {
            var resClassFields = resClass.getDeclaredFields();
            Class<?>[] classes = new Class[resClassFields.length];
            for (int i = 0; i < resClassFields.length; i++)
                classes[i] = resClassFields[i].getType();

            var constructor = resClass.getConstructor(classes);

            Object[] constructorArgs = new Object[classes.length];

            var mergeClassFields = mergeClass.getDeclaredFields();

            for (int i = 0; i < constructorArgs.length; i++) {
                var field = resClassFields[i];
                field.setAccessible(true);

                var value = field.get(entity);

                var replacement = Arrays.stream(mergeClassFields)
                        .filter(mcField -> mcField.getType().equals(field.getType()) && mcField.getName().equals(field.getName()))
                        .peek(mcField -> mcField.setAccessible(true))
                        .map(mcField -> {
                            try {
                                return mcField.get(mergeObj);
                            } catch (IllegalAccessException ex) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .findFirst();

                if (replacement.isPresent())
                    value = replacement.get();

                constructorArgs[i] = value;
            }
            return Optional.of(constructor.newInstance(constructorArgs));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return Optional.empty();
        }
    }
}
