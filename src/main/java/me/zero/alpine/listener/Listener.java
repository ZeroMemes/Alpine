package me.zero.alpine.listener;

import me.zero.alpine.bus.EventManager;
import me.zero.alpine.event.EventPriority;
import me.zero.alpine.event.Events;
import me.zero.alpine.exception.EventTypeException;
import me.zero.alpine.exception.ListenerTargetException;
import me.zero.alpine.util.Util;
import net.jodah.typetools.TypeResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Listener is an event callback wrapper that links event callbacks to their respective target types.
 * <p>
 * When using a method reference for the callback function, explicitly specifying the target event class may be
 * required. Consider the following example:
 * <pre>
 * public class EventHandler implements Subscriber {
 *   public EventHandler() {
 *     // Incorrect Usage
 *     App.EVENT_BUS.subscribe(new Listener&lt;String&gt;(this::doSomething));
 *   }
 *
 *   public void doSomething(Object object) { ... }
 * }
 * </pre>
 * Despite the explicit type parameter, the Listener target will be resolved as Object. (This is not an issue when the
 * Listener is a field, as the type parameter can be extracted from the generic signature). The solution to this is to
 * explicitly specify the target via a constructor parameter:
 * <pre>
 * public class EventHandler implements Subscriber {
 *   public EventHandler() {
 *     // Correct Usage
 *     App.EVENT_BUS.subscribe(new Listener&lt;&gt;(String.class, this::doSomething));
 *   }
 *
 *   public void doSomething(Object object) { ... }
 * }
 * </pre>
 *
 * @param <T> Target event type
 * @author Brady
 * @since 1.2
 */
/*
 Even though IntelliJ warns about placing @NotNull on a type parameter here, it is actually sufficient for Kotlin to
 deduce a non-null type for all usages of T.
 */
public final class Listener<@NotNull T> implements Consumer<T>, Comparable<Listener<?>> {

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
     * Priority of this {@link Listener}.
     *
     * @see EventPriority
     */
    private final int priority;

    public Listener(@NotNull Consumer<T> callback) {
        this(null, callback, emptyFilters());
    }

    public Listener(@NotNull Consumer<T> callback, int priority) {
        this(null, callback, priority, emptyFilters());
    }

    public Listener(@Nullable Class<T> target, @NotNull Consumer<T> callback) {
        this(target, callback, emptyFilters());
    }

    public Listener(@Nullable Class<T> target, @NotNull Consumer<T> callback, int priority) {
        this(target, callback, priority, emptyFilters());
    }

    @SafeVarargs
    public Listener(@NotNull Consumer<T> callback, @NotNull Predicate<? super T>... filters) {
        this(null, callback, filters);
    }

    @SafeVarargs
    public Listener(@NotNull Consumer<T> callback, int priority, @NotNull Predicate<? super T>... filters) {
        this(null, callback, priority, filters);
    }

    @SafeVarargs
    public Listener(@Nullable Class<T> target, @NotNull Consumer<T> callback, @NotNull Predicate<? super T>... filters) {
        this(target, callback, EventPriority.DEFAULT, filters);
    }

    /**
     * Creates a new {@link Listener} instance.
     *
     * @param target   The target event type. If {@code null}, an attempt will be made to automatically resolve the target.
     * @param callback The event callback function.
     * @param priority The priority value. See {@link EventPriority}.
     * @param filters  Checks used to validate the event object before the {@code callback} is invoked.
     * @throws EventTypeException If the event target isn't a {@link Events#validateEventType(Type) valid event type}.
     *                            This could happen unexpectedly if the target isn't specified, and the TypeResolver
     *                            resolves a generic superclass instead of the intended target. In this case, the event
     *                            target should be explicitly specified.
     */
    @SafeVarargs
    public Listener(@Nullable Class<T> target, @NotNull Consumer<T> callback, int priority, @NotNull Predicate<? super T>... filters) {
        this.callback = Util.predicated(callback, filters);
        this.priority = priority;
        this.target = Events.validateEventType(
            target == null ? TypeResolver.resolveRawArgument(Consumer.class, callback.getClass()) : target
        );
    }

    /**
     * Sets the target event type of this {@link Listener}. Used by {@link EventManager} to correct the target type
     * by resolving directly from the field's type parameter, preventing the need to explicitly specify the target
     * type when using a method reference to a method whose parameter isn't the exact event type.
     *
     * @param target The new target
     * @throws EventTypeException      If the new target isn't a valid event type
     * @throws ListenerTargetException If the existing target isn't assignable from the new target
     */
    public void setTarget(@NotNull Class<T> target) {
        Events.validateEventType(target);
        if (!this.target.isAssignableFrom(target)) {
            throw new ListenerTargetException("Current target type must be assignable from new target type");
        }
        this.target = target;
    }

    /**
     * Returns the type of the event that is targeted by this {@link Listener}.
     *
     * @return The target event type
     */
    public @NotNull Class<T> getTarget() {
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
        this.callback.accept(event);
    }

    @Override
    public int compareTo(@NotNull Listener<?> o) {
        // Listeners with higher priorities should come first, so negate the compare result
        return -Integer.compare(this.getPriority(), Objects.requireNonNull(o).getPriority());
    }

    @SuppressWarnings("unchecked")
    private static <T> Predicate<? super T>[] emptyFilters() {
        return (Predicate<? super T>[]) EMPTY_FILTERS;
    }
}
