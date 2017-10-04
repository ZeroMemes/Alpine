package me.zero.alpine.listener;

import me.zero.alpine.type.EventPriority;
import net.jodah.typetools.TypeResolver;

import java.util.function.Predicate;

/**
 * Used to contain event method data.
 *
 * @param <T> Target event type
 *
 * @author Brady
 * @since 1/21/2017 12:00 PM
 */
public final class Listener<T> implements EventHook<T> {

    /**
     * The target event class.
     */
    private final Class<T> target;

    /**
     * The "body" of this {@code Listener}
     */
    private final EventHook<T> hook;

    /**
     * Filters to be run on all of the events passed to this {@code Listener}
     */
    private final Predicate<T>[] filters;

    /**
     * Priority of this {@code Listener}
     *
     * @see EventPriority
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
     * Returns the target event class, represented
     * by the {@code <T>} class parameter.
     *
     * @return The target event class
     */
    public final Class<T> getTarget() {
        return this.target;
    }

    /**
     * Returns the priority of this listener. The priority
     * is used to determine the order of listener invocations
     * for a given event. The return value is limited by the
     * {@code HIGHEST} and {@code LOWEST} values of {@code EventPriority}.
     *
     * @see EventPriority
     *
     * @return Priority of Listener
     */
    public final byte getPriority() {
        return priority;
    }

    /**
     * Passes the event to this Listener's body after
     * all of the filters have passed their checks.
     *
     * @see EventHook
     *
     * @param event Event being called
     */
    @Override
    public final void invoke(T event) {
        if (filters.length > 0)
            for (Predicate<T> filter : filters)
                if (!filter.test(event))
                    return;

        this.hook.invoke(event);
    }
}
