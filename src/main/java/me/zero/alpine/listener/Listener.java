package me.zero.alpine.listener;

import me.zero.alpine.type.EventPriority;
import net.jodah.typetools.TypeResolver;

import java.util.function.Predicate;

/**
 * Used to contain event method data
 *
 * @author Brady
 * @since 1/21/2017 12:00 PM
 */
public final class Listener<T> implements EventHook<T> {

    /**
     * Class representation of the Event being
     * listened for.
     */
    private final Class<T> target;

    /**
     * The hook for this Listener
     */
    private final EventHook<T> hook;

    /**
     * Event filters
     */
    private final Predicate<T>[] filters;

    /**
     * Priority of Listener
     */
    private final byte priority;

    @SafeVarargs
    public Listener(EventHook<T> hook, Predicate<T>... filters) {
        this(hook, EventPriority.DEFAULT, filters);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public Listener(EventHook<T> hook, byte priority, Predicate<T>... filters) {
        this.hook = hook;
        this.priority = priority;
        this.target = (Class<T>) TypeResolver.resolveRawArgument(EventHook.class, hook.getClass());
        this.filters = filters;
    }

    /**
     * @return The class of T
     */
    public final Class<T> getTarget() {
        return this.target;
    }

    /**
     * @return Priority of Listener
     */
    public final byte getPriority() {
        return priority;
    }

    /**
     * Invokes the method that corresponds
     * with this Listener.
     *
     * @param event Event being called
     */
    @Override
    public final void invoke(T event) {
        for (Predicate<T> filter : filters)
            if (!filter.test(event))
                return;

        this.hook.invoke(event);
    }
}
