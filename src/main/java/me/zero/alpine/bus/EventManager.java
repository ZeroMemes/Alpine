package me.zero.alpine.bus;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.zero.alpine.event.Events;
import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.*;
import me.zero.alpine.listener.discovery.ListenerDiscoveryStrategy;
import me.zero.alpine.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of {@link EventBus}.
 *
 * @author Brady
 * @since 1.2
 */
public class EventManager implements EventBus {

    /**
     * Map containing all event subscriber instances have been previously subscribed and their associated Listener
     * field instances. This reduces the amount of reflection calls that would otherwise be required when adding a
     * subscriber to the event bus.
     */
    private final ConcurrentHashMap<Subscriber, List<Listener<?>>> subscriberListenerCache;

    /**
     * Map containing all event classes and the currently subscribed listeners.
     */
    private volatile Event2ListenersMap activeListeners;
    private final Object activeListenersWriteLock;

    // Settings specified through EventBusBuilder
    protected final String name;
    protected final boolean parentDiscovery;
    protected final List<ListenerDiscoveryStrategy> discoveryStrategies;
    protected final EventDispatcher eventDispatcher;
    protected final ListenerListFactory listenerListFactory;

    public EventManager(@NotNull String name) {
        this(new EventBusBuilder<>().setName(name));
    }

    public EventManager(@NotNull EventBusBuilder<?> builder) {
        Objects.requireNonNull(builder);

        this.subscriberListenerCache = new ConcurrentHashMap<>();
        this.activeListeners = new Event2ListenersMap();
        this.activeListenersWriteLock = new Object();

        // Copy settings from builder
        this.name = builder.getName();
        this.parentDiscovery = builder.isParentDiscovery();
        this.eventDispatcher = builder.getExceptionHandler()
            .map(EventDispatcher::withExceptionHandler)
            .orElseGet(EventDispatcher::fastEventDispatcher);
        this.discoveryStrategies = new ArrayList<>(builder.getDiscoveryStrategies());

        final ListenerListFactory factory = builder.getListenerListFactory();

        // Wrap the factory in ListenerGroup if superListeners is enabled
        if (builder.isSuperListeners()) {
            this.listenerListFactory = new ListenerListFactory() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> @NotNull ListenerList<T> create(Class<T> cls) {
                    ListenerGroup<T> group = new ListenerGroup<>(factory.create(cls));
                    EventManager.this.activeListeners.forEach((activeTarget, activeGroup) -> {
                        // Link target to inherited types
                        if (activeTarget.isAssignableFrom(cls)) {
                            group.addChild((ListenerGroup<? super T>) activeGroup);
                        }
                        // Link inheriting types to target
                        if (cls.isAssignableFrom(activeTarget)) {
                            ((ListenerGroup<? extends T>) activeGroup).addChild(group);
                        }
                    });
                    return group;
                }
            };
        } else {
            this.listenerListFactory = factory;
        }
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public void subscribe(@NotNull Subscriber subscriber) {
        this.subscriberListenerCache.computeIfAbsent(subscriber, this::getListeners).forEach(this::subscribe);
    }

    @Override
    public <T> void subscribe(@NotNull Listener<T> listener) {
        this.getOrCreateListenerList(listener.getTarget()).add(listener);
    }

    @Override
    public void unsubscribe(@NotNull Subscriber subscriber) {
        final List<Listener<?>> subscriberListeners = this.subscriberListenerCache.get(subscriber);
        if (subscriberListeners != null) {
            subscriberListeners.forEach(this::unsubscribe);
        }
    }

    @Override
    public <T> void unsubscribe(@NotNull Listener<T> listener) {
        final ListenerList<T> list = this.activeListeners.get(listener.getTarget());
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void post(@NotNull T event) {
        this.getOrCreateListenerList((Class<T>) event.getClass()).post(event, this.eventDispatcher);
    }

    @Override
    public String toString() {
        return "EventManager{name='" + this.name + "'}";
    }

    private List<Listener<?>> getListeners(Subscriber subscriber) {
        // TODO: Per-class candidate caching

        return Collections.unmodifiableList(
            // Get all super-classes of 'subscriber' that inherit Subscriber (if 'parentDiscovery' is enabled)
            this.getSubscriberHierarchy(subscriber.getClass())
                // Apply each discovery strategy to each class, and use flatMap to create a stream of candidates
                .flatMap(cls -> this.discoveryStrategies.stream().flatMap(strategy -> strategy.findAll(cls)))
                // Bind the subscriber instance to each candidate to its Listener instances
                .flatMap(candidate -> candidate.bind(subscriber))
                .collect(Collectors.toList())
        );
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<? extends Subscriber>> getSubscriberHierarchy(final Class<? extends Subscriber> cls) {
        if (!this.parentDiscovery) {
            return Stream.of(cls);
        }
        return Util.flattenHierarchy(cls).stream()
            .filter(Subscriber.class::isAssignableFrom)
            .map(c -> (Class<? extends Subscriber>) c);
    }

    private <T> ListenerList<T> getOrCreateListenerList(Class<T> target) {
        // This method of initialization results in much faster dispatch than 'computeIfAbsent'
        // It also guarantees that only one thread can call 'listenerListFactory.create(...)' at a time
        final ListenerList<T> existing = this.activeListeners.get(target);
        if (existing != null) {
            return existing;
        }
        synchronized (this.activeListenersWriteLock) {
            // Fetch the list again, as it could've been initialized since the lock was released.
            final ListenerList<T> list = this.activeListeners.get(target);
            if (list == null) {
                // Validate the event type, throwing an IllegalArgumentException if it is invalid
                Util.catchAndRethrow(() -> Events.validateEventType(target), IllegalArgumentException::new);

                final ListenerList<T> newList = this.listenerListFactory.create(target);

                // If insertion of a new key will require a rehash, then clone the map and reassign the field.
                if (this.activeListeners.needsRehash()) {
                    final Event2ListenersMap newMap = this.activeListeners.clone();
                    newMap.put(target, newList);
                    this.activeListeners = newMap;
                } else {
                    this.activeListeners.put(target, newList);
                }

                return newList;
            } else {
                return list;
            }
        }
    }

    public static @NotNull EventBusBuilder<EventBus> builder() {
        return new EventBusBuilder<>();
    }

    private static final class Event2ListenersMap extends Reference2ObjectOpenHashMap<Class<?>, ListenerList<?>> {

        @SuppressWarnings("unchecked")
        private <T> ListenerList<T> get(final Class<T> target) {
            return (ListenerList<T>) super.get(target);
        }

        /**
         * @return {@code true} if the next insertion of a new key will require a rehash.
         */
        private boolean needsRehash() {
            return this.size >= this.maxFill;
        }

        @Override
        public Event2ListenersMap clone() {
            return (Event2ListenersMap) super.clone();
        }
    }
}
