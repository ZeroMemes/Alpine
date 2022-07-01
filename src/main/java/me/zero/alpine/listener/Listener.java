package me.zero.alpine.listener;

import me.zero.alpine.bus.EventManager;
import me.zero.alpine.event.EventPriority;
import net.jodah.typetools.TypeResolver;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A wrapper body that is used to define new event listeners. When using a method reference for the callback function,
 * specifying the target event class explicitly may be required.
 *
 * @param <T> Target event type
 * @author Brady
 * @since 1/21/2017
 */
public final class Listener<T> implements Consumer<T> {

    private static final Predicate<?>[] EMPTY_FILTERS = new Predicate[0];

    /**
     * The type of the target event.
     */
    private Class<T> target;

    /**
     * The body of this {@link Listener}, called when all filters, if any, pass.
     */
    private final Consumer<T> callback;

    /**
     * The filters that events being posted to this {@link Listener} are tested against.
     */
    private final Predicate<? super T>[] filters;

    /**
     * Priority of this {@link Listener}.
     *
     * @see EventPriority
     */
    private final int priority;

    @SuppressWarnings("unchecked")
    public Listener(Consumer<T> callback) {
        this(null, callback, (Predicate<? super T>[]) EMPTY_FILTERS);
    }

    @SuppressWarnings("unchecked")
    public Listener(Consumer<T> callback, int priority) {
        this(null, callback, priority, (Predicate<? super T>[]) EMPTY_FILTERS);
    }

    @SuppressWarnings("unchecked")
    public Listener(Class<T> target, Consumer<T> callback) {
        this(target, callback, (Predicate<? super T>[]) EMPTY_FILTERS);
    }

    @SuppressWarnings("unchecked")
    public Listener(Class<T> target, Consumer<T> callback, int priority) {
        this(target, callback, priority, (Predicate<? super T>[]) EMPTY_FILTERS);
    }

    @SafeVarargs
    public Listener(Consumer<T> callback, Predicate<? super T>... filters) {
        this(null, callback, filters);
    }

    @SafeVarargs
    public Listener(Consumer<T> callback, int priority, Predicate<? super T>... filters) {
        this(null, callback, priority, filters);
    }

    @SafeVarargs
    public Listener(Class<T> target, Consumer<T> callback, Predicate<? super T>... filters) {
        this(target, callback, EventPriority.DEFAULT, filters);
    }

    /**
     * Creates a new {@link Listener} instance.
     *
     * @param target   The target event type. If {@code null}, an attempt will be made to automatically resolve the target.
     * @param callback The event callback function.
     * @param priority The priority value. See {@link EventPriority}.
     * @param filters  Checks used to validate the event object before the {@code callback} is invoked.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public Listener(Class<T> target, Consumer<T> callback, int priority, Predicate<? super T>... filters) {
        this.callback = callback;
        this.priority = priority;
        this.filters = filters;
        this.target = target == null
            ? (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, callback.getClass())
            : target;
    }

    /**
     * Sets the target event type of this {@link Listener}. Used by {@link EventManager} to correct the target type
     * by resolving directly from the field's type parameter, preventing the need to explicitly specify the target
     * type when using a method reference to a method whose parameter isn't the exact event type.
     *
     * @param target The new target
     * @throws IllegalArgumentException if the existing target isn't assignable from the new target
     */
    public void setTarget(Class<T> target) {
        if (!this.target.isAssignableFrom(target)) {
            throw new IllegalArgumentException("Current target type must be assignable from new target type");
        }
        this.target = target;
    }

    /**
     * Returns the type of the event that is targeted by this {@link Listener}.
     *
     * @return The target event type
     */
    public Class<T> getTarget() {
        return this.target;
    }

    /**
     * Returns the priority of this {@link Listener}. See {@link EventPriority} for a description of this value.
     *
     * @return The priority of this {@link Listener}
     * @see EventPriority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Called during the event posting sequence. Verifies that the event can be accepted by testing it against this
     * {@link Listener}'s filters, and if so, proceeds with passing the event to this {@link Listener}'s body function.
     *
     * @param event Event being posted
     */
    @Override
    public void accept(T event) {
        for (Predicate<? super T> filter : this.filters) {
            if (!filter.test(event)) {
                return;
            }
        }
        this.callback.accept(event);
    }
}
