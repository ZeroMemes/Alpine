package me.zero.alpine.listener;

import me.zero.alpine.event.EventPriority;
import net.jodah.typetools.TypeResolver;

import java.util.function.Predicate;

/**
 * A wrapper body that is used to define new event listeners. When a body is a method reference,
 * there isn't a guarantee that the target type will be interpreted correctly, and therefore the
 * more explicit implementation of this class, {@link MethodRefListener}, should be used instead.
 *
 * @param <T> Target event type
 *
 * @author Brady
 * @since 1/21/2017
 */
public class Listener<T> implements EventHook<T> {

    /**
     * The type of the target event.
     */
    private final Class<T> target;

    /**
     * The body of this {@link Listener}, called when all filters, if any, pass.
     */
    private final EventHook<T> hook;

    /**
     * Various "filters" that events being posted to this {@link Listener} are tested against.
     */
    private final Predicate<T>[] filters;

    /**
     * Priority of this {@code Listener}
     *
     * @see EventPriority
     */
    private final int priority;

    @SafeVarargs
    public Listener(EventHook<T> hook, Predicate<T>... filters) {
        this(hook, EventPriority.DEFAULT, filters);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public Listener(EventHook<T> hook, int priority, Predicate<T>... filters) {
        this.hook = hook;
        this.priority = priority;
        this.target = (Class<T>) TypeResolver.resolveRawArgument(EventHook.class, hook.getClass());
        this.filters = filters;
    }

    /**
     * Returns the type of the event that is targeted by this {@link Listener}.
     * This is defined by the {@link T} generic parameter.
     *
     * @return The target event type
     */
    public Class<T> getTarget() {
        return this.target;
    }

    /**
     * Returns the priority of this {@link Listener}. A higher literal value is reflective
     * of a higher priority, and therefore the {@link Listener} will be called sooner in the
     * event posting sequence.
     *
     * @see EventPriority
     *
     * @return Priority of Listener
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Called during the event posting sequence. Verifies that the event is acceptable by
     * testing it against the filters defined by this event listener, and if so, makes
     * the final event pass to this listener's handling body.
     *
     * @see EventHook
     *
     * @param event Event being called
     */
    @Override
    public void invoke(T event) {
        if (filters.length > 0) {
            for (Predicate<T> filter : filters) {
                if (!filter.test(event)) {
                    return;
                }
            }
        }
        this.hook.invoke(event);
    }
}
