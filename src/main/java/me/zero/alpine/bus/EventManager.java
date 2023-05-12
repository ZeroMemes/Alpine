package me.zero.alpine.bus;

import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerGroup;
import me.zero.alpine.listener.Subscribe;
import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of {@link EventBus}.
 *
 * @author Brady
 * @since 1/19/2017
 */
public class EventManager implements EventBus {

    /**
     * Map containing all event subscriber instances have been previously subscribed and their associated Listener
     * field instances. This reduces the amount of reflection calls that would otherwise be required when adding a
     * subscriber to the event bus.
     */
    protected final ConcurrentHashMap<EventSubscriber, List<Listener<?>>> subscriberListenerCache;

    /**
     * Map containing all event classes and the currently subscribed listeners.
     */
    protected final ConcurrentHashMap<Class<?>, ListenerGroup<?>> activeListeners;

    /**
     * The name of this bus.
     */
    protected final String name;

    /**
     * Whether to search superclasses of an instance for Listener fields. Due to the caching functionality provided by
     * {@link #subscriberListenerCache}, modifying this field outside the constructor can result in inconsistent
     * behavior when adding a {@link EventSubscriber} to the bus. It is therefore recommended that subclasses of this
     * {@link EventBus} implementation set the desired value of this flag in their constructor, prior to when any
     * {@link EventSubscriber}s would be added via any of the relevant {@code subscribe} methods.
     */
    protected boolean recursiveDiscovery;

    /**
     * Whether to post an event to all Listeners that target a supertype of the event.
     */
    protected boolean superListeners;

    public EventManager(String name) {
        this(name, false, false);
    }

    EventManager(String name, boolean recursiveDiscovery, boolean superListeners) {
        this.subscriberListenerCache = new ConcurrentHashMap<>();
        this.activeListeners = new ConcurrentHashMap<>();
        this.name = name;
        this.recursiveDiscovery = recursiveDiscovery;
        this.superListeners = superListeners;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void subscribe(EventSubscriber subscriber) {
        this.subscriberListenerCache.computeIfAbsent(subscriber, this::getListeners).forEach(this::subscribe);
    }

    @Override
    public <T> void subscribe(Listener<T> listener) {
        this.getOrCreateListenerGroup(listener.getTarget()).add(listener);
    }

    @Override
    public void unsubscribe(EventSubscriber subscriber) {
        List<Listener<?>> subscriberListeners = this.subscriberListenerCache.get(subscriber);
        if (subscriberListeners != null) {
            subscriberListeners.forEach(this::unsubscribe);
        }
    }

    @Override
    public <T> void unsubscribe(Listener<T> listener) {
        ListenerGroup<?> list = this.activeListeners.get(listener.getTarget());
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void post(T event) {
        this.getOrCreateListenerGroup((Class<T>) event.getClass()).post(event);
    }

    @Override
    public String toString() {
        return "EventManager{name='" + this.name + "'}";
    }

    protected List<Listener<?>> getListeners(EventSubscriber subscriber) {
        Class<?> cls = subscriber.getClass();
        Stream<Field> fields = Stream.empty();
        do {
            fields = Stream.concat(fields, getListenerFields(cls));
            cls = cls.getSuperclass();
        } while (this.recursiveDiscovery && cls != null);

        return Collections.unmodifiableList(
            fields.map(field -> asListener(subscriber, field)).collect(Collectors.toList())
        );
    }

    @SuppressWarnings("unchecked")
    protected <T> ListenerGroup<T> getOrCreateListenerGroup(Class<T> target) {
        // This method of initialization results in much faster dispatch than 'computeIfAbsent'
        // It also guarantees that only one thread can call 'createListenerGroup' at a time
        final ListenerGroup<T> existing = (ListenerGroup<T>) this.activeListeners.get(target);
        if (existing != null) {
            return existing;
        }
        synchronized (this.activeListeners) {
            // Fetch the group again, as it could've been initialized since the lock was released.
            final ListenerGroup<T> group = (ListenerGroup<T>) this.activeListeners.get(target);
            if (group == null) {
                ListenerGroup<T> list = this.createListenerGroup(target);
                this.activeListeners.put(target, list);
                return list;
            } else {
                return group;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> ListenerGroup<T> createListenerGroup(Class<T> target) {
        ListenerGroup<T> group = new ListenerGroup<>();
        if (this.superListeners) {
            this.activeListeners.forEach((activeTarget, activeGroup) -> {
                // Link target to inherited types
                if (activeTarget.isAssignableFrom(target)) {
                    group.addChild((ListenerGroup<? super T>) activeGroup);
                }
                // Link inheriting types to target
                if (target.isAssignableFrom(activeTarget)) {
                    ((ListenerGroup<? extends T>) activeGroup).addChild(group);
                }
            });
        }
        return group;
    }

    protected static Stream<Field> getListenerFields(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredFields()).filter(EventManager::isListenerField);
    }

    protected static boolean isListenerField(Field field) {
        return field.isAnnotationPresent(Subscribe.class)
            && field.getType().equals(Listener.class)
            && !Modifier.isStatic(field.getModifiers());
    }

    @SuppressWarnings("unchecked")
    protected static <T> Listener<T> asListener(EventSubscriber subscriber, Field field) {
        try {
            if (!(field.getGenericType() instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Listener fields must have a specified type parameter!");
            }

            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Listener<T> listener = (Listener<T>) field.get(subscriber);
            field.setAccessible(accessible);

            // Resolve the actual target type from the field type parameter, and update the Listener target
            Class<T> target = (Class<T>) TypeResolver.resolveRawArgument(field.getGenericType(), Listener.class);
            listener.setTarget(target);

            return listener;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access Listener field");
        }
    }

    public static EventBusBuilder<EventBus> builder() {
        return new EventBusBuilder<>();
    }
}
