package me.zero.alpine.bus;

import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    protected final Map<EventSubscriber, List<Listener<?>>> subscriberListenerCache = new ConcurrentHashMap<>();

    /**
     * Map containing all event classes and the currently subscribed listeners.
     */
    protected final Map<Class<?>, CopyOnWriteArrayList<Listener<?>>> activeListeners = new ConcurrentHashMap<>();

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
    public void subscribe(Listener<?> listener) {
        List<Listener<?>> listeners = this.activeListeners.computeIfAbsent(listener.getTarget(), target -> new CopyOnWriteArrayList<>());

        if (listeners.contains(listener)) {
            return;
        }

        int index = Collections.binarySearch(listeners, listener);
        if (index < 0) {
            index = -index - 1;
        }
        listeners.add(index, listener);
    }

    @Override
    public void unsubscribe(EventSubscriber subscriber) {
        List<Listener<?>> subscriberListeners = this.subscriberListenerCache.get(subscriber);
        if (subscriberListeners == null)
            return;

        subscriberListeners.forEach(this::unsubscribe);
    }

    @Override
    public void unsubscribe(Listener<?> listener) {
        List<Listener<?>> eventListeners = this.activeListeners.get(listener.getTarget());
        if (eventListeners == null)
            return;

        eventListeners.remove(listener);
        if (eventListeners.isEmpty()) {
            this.activeListeners.remove(listener.getTarget());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void post(Object event) {
        if (this.superListeners) {
            // Iterate through all active listeners. If the event is assignable to the target, post as the target type.
            this.activeListeners.forEach((target, listeners) -> {
                if (target.isAssignableFrom(event.getClass())) {
                    listeners.forEach(listener -> ((Listener<Object>) listener).accept(event));
                }
            });
        } else {
            CopyOnWriteArrayList<Listener<?>> listeners = this.activeListeners.get(event.getClass());
            if (listeners != null) {
                listeners.forEach(listener -> ((Listener<Object>) listener).accept(event));
            }
        }
    }

    @Override
    public String toString() {
        return "EventManager{name='" + this.name + "'}";
    }

    protected List<Listener<?>> getListeners(EventSubscriber subscriber) {
        Class<?> cls = subscriber.getClass();
        Stream<Field> fields = Stream.empty();
        do {
            fields = Stream.concat(fields, getListenersFields(cls));
            cls = cls.getSuperclass();
        } while (this.recursiveDiscovery && cls != null);

        return Collections.unmodifiableList(
            fields.map(field -> asListener(subscriber, field)).collect(Collectors.toList())
        );
    }

    protected static Stream<Field> getListenersFields(Class<?> cls) {
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
