package me.zero.alpine.bus;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;
import me.zero.alpine.listener.Subscriber;

import java.util.Objects;

/**
 * @author Brady
 * @since 6/22/2022
 */
public final class EventBusBuilder<T extends EventBus> {

    String name = null;
    boolean recursiveDiscovery = false;
    boolean superListeners = false;
    ListenerExceptionHandler exceptionHandler = ListenerExceptionHandler.DEFAULT;
    private boolean attachable = false;

    EventBusBuilder() {}

    /**
     * Sets the name of the {@link EventBus}.
     *
     * @param name The name
     * @return This builder
     * @see EventBus#name()
     */
    public EventBusBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Enables recursive {@link Listener} discovery, allowing {@link Listener} fields in superclasses of
     * {@link Subscriber}s to be discovered and registered.
     *
     * @return This builder
     */
    public EventBusBuilder<T> setRecursiveDiscovery() {
        this.recursiveDiscovery = true;
        return this;
    }

    /**
     * Enables {@link Listener} targeting an event's supertype to be invoked with the event in addition to
     * {@link Listener}s directly targeting the event type.
     *
     * @return This builder
     */
    public EventBusBuilder<T> setSuperListeners() {
        this.superListeners = true;
        return this;
    }

    /**
     * Sets the exception handler that will be invoked when an exception is thrown by a Listener.
     *
     * @see ListenerExceptionHandler#DEFAULT
     *
     * @param exceptionHandler The exception handler
     * @return This builder
     */
    public EventBusBuilder<T> setExceptionHandler(ListenerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Causes this builder to create an {@link EventBus} which implements {@link AttachableEventBus}.
     *
     * @return This builder
     */
    @SuppressWarnings("unchecked")
    public EventBusBuilder<AttachableEventBus> setAttachable() {
        this.attachable = true;
        // Illegal? Maybe.
        return (EventBusBuilder<AttachableEventBus>) this;
    }

    /**
     * @return The constructed {@link EventBus}
     */
    @SuppressWarnings("unchecked")
    public T build() {
        Objects.requireNonNull(this.name);
        return this.attachable
            ? (T) new AttachableEventManager(this)
            : (T) new EventManager(this);
    }
}
