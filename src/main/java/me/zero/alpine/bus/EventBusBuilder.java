package me.zero.alpine.bus;

import me.zero.alpine.listener.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Brady
 * @since 2.0.0
 */
public final class EventBusBuilder<T extends EventBus> {

    String name = null;
    boolean recursiveDiscovery = false;
    boolean superListeners = false;
    ListenerExceptionHandler exceptionHandler = ListenerExceptionHandler.DEFAULT;
    ListenerListFactory listenerListFactory = ListenerListFactory.DEFAULT;
    private boolean attachable = false;

    EventBusBuilder() {}

    /**
     * Sets the name of the {@link EventBus}.
     *
     * @param name The name
     * @return This builder
     * @see EventBus#name()
     * @since 2.0.0
     */
    public @NotNull EventBusBuilder<T> setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    /**
     * Enables recursive {@link Listener} discovery, allowing {@link Listener} fields in superclasses of
     * {@link Subscriber}s to be discovered and registered.
     *
     * @return This builder
     * @since 2.0.0
     */
    public @NotNull EventBusBuilder<T> setRecursiveDiscovery() {
        this.recursiveDiscovery = true;
        return this;
    }

    /**
     * Enables {@link Listener} targeting an event's supertype to be invoked with the event in addition to
     * {@link Listener}s directly targeting the event type.
     *
     * @return This builder
     * @since 2.0.0
     */
    public @NotNull EventBusBuilder<T> setSuperListeners() {
        this.superListeners = true;
        return this;
    }

    /**
     * Sets the exception handler that will be invoked when an exception is thrown by a Listener. The specified
     * exception handler may be {@code null}, indicating that no explicit exception handling is to occur, and
     * exceptions should just propagate upwards.
     *
     * @param exceptionHandler The exception handler
     * @return This builder
     * @see ListenerExceptionHandler#DEFAULT
     * @since 3.0.0
     */
    public @NotNull EventBusBuilder<T> setExceptionHandler(@Nullable ListenerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Disables exception handling. Equivalent to {@code setExceptionHandler(null)}.
     *
     * @return This builder
     * @since 3.0.0
     */
    public @NotNull EventBusBuilder<T> noExceptionHandler() {
        return this.setExceptionHandler(null);
    }

    /**
     * Sets the factory that will be used to create {@link ListenerList} objects for each event type.
     *
     * @param factory The factory
     * @return This builder
     * @see ListenerListFactory#DEFAULT
     * @since 3.0.0
     */
    public @NotNull EventBusBuilder<T> setListenerListFactory(@NotNull ListenerListFactory factory) {
        Objects.requireNonNull(factory);
        this.listenerListFactory = factory;
        return this;
    }

    /**
     * Causes this builder to create an {@link EventBus} which implements {@link AttachableEventBus}.
     *
     * @return This builder
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public @NotNull EventBusBuilder<AttachableEventBus> setAttachable() {
        this.attachable = true;
        // Illegal? Maybe.
        return (EventBusBuilder<AttachableEventBus>) this;
    }

    /**
     * @return The constructed {@link EventBus}
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public @NotNull T build() {
        Objects.requireNonNull(this.name);
        return this.attachable
            ? (T) new AttachableEventManager(this)
            : (T) new EventManager(this);
    }
}
