package me.zero.alpine.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Brady
 * @since 5/10/2023
 */
public final class Consumers {

    private Consumers() {}

    @SafeVarargs
    public static <T> Consumer<T> predicated(Consumer<T> consumer, Predicate<? super T>... predicates) {
        switch (predicates.length) {
            case 0: {
                return consumer;
            }
            case 1: {
                final Predicate<? super T> p0 = predicates[0];
                return event -> {
                    if (p0.test(event)) {
                        consumer.accept(event);
                    }
                };
            }
            case 2: {
                final Predicate<? super T> p0 = predicates[0], p1 = predicates[1];
                return event -> {
                    if (p0.test(event) && p1.test(event)) {
                        consumer.accept(event);
                    }
                };
            }
            default: {
                return event -> {
                    for (Predicate<? super T> filter : predicates) {
                        if (!filter.test(event)) {
                            return;
                        }
                    }
                    consumer.accept(event);
                };
            }
        }
    }
}
