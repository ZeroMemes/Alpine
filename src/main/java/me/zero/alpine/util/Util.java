package me.zero.alpine.util;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility methods for internal use only with no guarantee of API stability.
 *
 * @author Brady
 * @since 3.0.0
 */
@ApiStatus.Internal
public final class Util {

    private Util() {}

    private static final WeakHashMap<Class<?>, Set<Class<?>>> HIERARCHY_CACHE;
    private static final MethodHandles.Lookup LOOKUP;

    static {
        HIERARCHY_CACHE = new WeakHashMap<>();
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);

            // Java 16 compatible method of getting IMPL_LOOKUP
            Field impl = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            LOOKUP = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(impl), unsafe.staticFieldOffset(impl));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the {@code TRUSTED} Lookup instance
     */
    public static MethodHandles.Lookup getLookup() {
        return LOOKUP;
    }

    public static <T> Callable<T> lazy(Callable<T> initializer) {
        return new Callable<T>() {

            private volatile T value;

            @Override
            public T call() throws Exception {
                if (this.value == null) {
                    synchronized (this) {
                        if (this.value == null) {
                            this.value = initializer.call();
                        }
                    }
                }
                return this.value;
            }
        };
    }

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

    public static <T> T catchAndRethrow(ThrowingSupplier<T> function, Function<Throwable, ? extends RuntimeException> wrapper) {
        try {
            return function.get();
        } catch (Throwable cause) {
            throw wrapper.apply(cause);
        }
    }

    public static <T> Iterator<T> arrayIterator(final T[] array) {
        return new Iterator<T>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return this.i != array.length;
            }

            @Override
            public T next() {
                return array[this.i++];
            }
        };
    }

    public static <T> Iterator<T> singletonIterator(final T element) {
        return Collections.singleton(element).iterator();
    }

    public synchronized static <T> Set<Class<?>> flattenHierarchy(final Class<T> cls) {
        final Set<Class<?>> cached = HIERARCHY_CACHE.get(cls);
        if (cached != null) {
            return cached;
        }

        final Set<Class<?>> flattened = new HashSet<>();
        flattened.add(cls);
        if (cls.getSuperclass() != null) {
            flattened.addAll(flattenHierarchy(cls.getSuperclass()));
        }
        for (Class<?> iface : cls.getInterfaces()) {
            flattened.addAll(flattenHierarchy(iface));
        }

        HIERARCHY_CACHE.put(cls, flattened);
        return flattened;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }
}
