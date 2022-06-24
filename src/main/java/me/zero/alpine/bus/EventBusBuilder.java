package me.zero.alpine.bus;

import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;

import java.util.Objects;

/**
 * @author Brady
 * @since 6/22/2022
 */
public final class EventBusBuilder<T extends EventBus> {

    private String name = null;
    private boolean recursiveDiscovery = false;
    private boolean superListeners = false;
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
     * {@link EventSubscriber}s to be discovered and registered.
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
            ? (T) new AttachableEventManager(this.name, this.recursiveDiscovery, this.superListeners)
            : (T) new EventManager(this.name, this.recursiveDiscovery, this.superListeners);
    }
}
