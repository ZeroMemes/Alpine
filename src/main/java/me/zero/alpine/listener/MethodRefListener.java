package me.zero.alpine.listener;

import java.util.function.Predicate;

/**
 * A special implementation of {@link Listener} that is compatible
 * with method reference listener bodies. The issue with using method
 * references in standard Listeners is that it believes the target type
 * is that of the base method reference class.
 *
 * @author Brady
 * @since 9/15/2018
 */
public class MethodRefListener<T> extends Listener<T> {

    private final Class<T> target;

    @SafeVarargs
    public MethodRefListener(Class<T> target, EventHook<T> hook, Predicate<T>... filters) {
        super(hook, filters);
        this.target = target;
    }

    @SafeVarargs
    public MethodRefListener(Class<T> target, EventHook<T> hook, int priority, Predicate<T>... filters) {
        super(hook, priority, filters);
        this.target = target;
    }

    @Override
    public Class<T> getTarget() {
        return this.target;
    }
}
