package me.zero.alpine.util;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * @author Brady
 * @since 5/14/2023
 */
public final class Util {

    private Util() {}

    private static final Unsafe UNSAFE;
    private static final MethodHandles.Lookup LOOKUP;

    static {
        try {
            Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            UNSAFE = (Unsafe) unsafe.get(null);

            // Java 16 compatible method of getting IMPL_LOOKUP
            Field impl = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(UNSAFE.staticFieldBase(impl), UNSAFE.staticFieldOffset(impl));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    /**
     * @return The {@code TRUSTED} Lookup instance
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
}
