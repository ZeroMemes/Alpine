package me.zero.alpine.bus;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.*;
import me.zero.alpine.listener.discovery.ListenerDiscoveryStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    protected final ConcurrentHashMap<Subscriber, List<Listener<?>>> subscriberListenerCache;

    /**
     * Map containing all event classes and the currently subscribed listeners.
     */
    protected volatile Event2ListenersMap activeListeners;

    protected final Object activeListenersWriteLock;

    /**
     * The name of this bus.
     */
    protected final String name;

    /**
     * Whether to search superclasses of an instance for Listener fields.
     */
    protected final boolean recursiveDiscovery;

    protected final List<ListenerDiscoveryStrategy> discoveryStrategies;

    protected final EventDispatcher eventDispatcher;

    protected final ListenerListFactory listenerListFactory;

    public EventManager(@NotNull String name) {
        this(new EventBusBuilder<>().setName(name));
    }

    public EventManager(@NotNull EventBusBuilder<?> builder) {
        this.subscriberListenerCache = new ConcurrentHashMap<>();
        this.activeListeners = new Event2ListenersMap();
        this.activeListenersWriteLock = new Object();

        // TODO: Specify through builder
        this.discoveryStrategies = Collections.unmodifiableList(Arrays.asList(
            ListenerDiscoveryStrategy.subscribeFields(),
            ListenerDiscoveryStrategy.subscribeMethods()
        ));

        // Copy settings from builder
        this.name = builder.name;
        this.recursiveDiscovery = builder.recursiveDiscovery;
        this.eventDispatcher = builder.exceptionHandler == null
            ? EventDispatcher.noExceptionHandler()
            : EventDispatcher.withExceptionHandler(builder.exceptionHandler);

        final ListenerListFactory factory = builder.listenerListFactory;

        // Wrap the factory in ListenerGroup if superListeners is enabled
        if (builder.superListeners) {
            this.listenerListFactory = new ListenerListFactory() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> ListenerList<T> create(Class<T> cls) {
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
        List<Listener<?>> subscriberListeners = this.subscriberListenerCache.get(subscriber);
        if (subscriberListeners != null) {
            subscriberListeners.forEach(this::unsubscribe);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void unsubscribe(@NotNull Listener<T> listener) {
        ListenerList<T> list = (ListenerList<T>) this.activeListeners.get(listener.getTarget());
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

    protected List<Listener<?>> getListeners(Subscriber subscriber) {
        // TODO: Per-class candidate caching

        return Collections.unmodifiableList(
            // Get all super-classes of 'subscriber' that inherit Subscriber
            this.getSubscriberHierarchy(subscriber.getClass())
                // Apply each discovery strategy to each class, and use flatMap to create a stream of candidates
                .flatMap(cls -> this.discoveryStrategies.stream().flatMap(strategy -> strategy.findAll(cls)))
                // Bind the subscriber instance to each candidate to get a Listener instance
                .map(candidate -> candidate.bind(subscriber))
                .collect(Collectors.toList())
        );
    }

    protected Stream<Class<? extends Subscriber>> getSubscriberHierarchy(final Class<? extends Subscriber> cls) {
        if (!this.recursiveDiscovery) {
            return Stream.of(cls);
        }

        return StreamSupport.stream(new Spliterators.AbstractSpliterator<Class<? extends Subscriber>>(
            Long.MAX_VALUE, Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL
        ) {
            private Class<?> curr = cls;

            @SuppressWarnings("unchecked")
            @Override
            public boolean tryAdvance(Consumer<? super Class<? extends Subscriber>> action) {
                if (Subscriber.class.isAssignableFrom(this.curr)) {
                    action.accept((Class<? extends Subscriber>) this.curr);
                    this.curr = this.curr.getSuperclass();
                    return true;
                }
                return false;
            }
        }, false);
    }

    @SuppressWarnings("unchecked")
    protected <T> ListenerList<T> getOrCreateListenerList(Class<T> target) {
        // This method of initialization results in much faster dispatch than 'computeIfAbsent'
        // It also guarantees that only one thread can call 'createListenerList' at a time
        final ListenerList<T> existing = (ListenerList<T>) this.activeListeners.get(target);
        if (existing != null) {
            return existing;
        }
        synchronized (this.activeListenersWriteLock) {
            // Fetch the group again, as it could've been initialized since the lock was released.
            final ListenerList<T> group = (ListenerList<T>) this.activeListeners.get(target);
            if (group == null) {
                ListenerList<T> list = this.listenerListFactory.create(target);

                // If insertion of a new key will require a rehash, then clone the map and reassign the field.
                if (this.activeListeners.needsRehash()) {
                    final Event2ListenersMap newMap = this.activeListeners.clone();
                    newMap.put(target, list);
                    this.activeListeners = newMap;
                } else {
                    this.activeListeners.put(target, list);
                }

                return list;
            } else {
                return group;
            }
        }
    }

    public static @NotNull EventBusBuilder<EventBus> builder() {
        return new EventBusBuilder<>();
    }

    protected static final class Event2ListenersMap extends Reference2ObjectOpenHashMap<Class<?>, ListenerList<?>> {

        /**
         * @return {@code true} if the next insertion of a new key will require a rehash.
         */
        public boolean needsRehash() {
            return this.size >= this.maxFill;
        }

        @Override
        public Event2ListenersMap clone() {
            return (Event2ListenersMap) super.clone();
        }
    }
}
