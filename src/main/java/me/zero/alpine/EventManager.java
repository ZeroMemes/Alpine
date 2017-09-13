package me.zero.alpine;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.type.EventPriority;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of {@code EventBus}
 *
 * @author Brady
 * @since 1/19/2017 12:00 PM
 */
public class EventManager implements EventBus {

    /**
     * Map containing all Listeners for objects, this is used to prevent
     * reflection calls when subscribing/unsubscribing
     */
    private final Map<Object, List<Listener>> SUBSCRIPTION_CACHE = new HashMap<>();

    /**
     * Map containing all event classes and their corresponding listeners
     */
    private final Map<Class<?>, List<Listener>> SUBSCRIPTION_MAP = new HashMap<>();

    /**
     * Holds the list of attached event buses
     */
    private final List<EventBus> ATTACHED_BUSES = new ArrayList<>();

    @Override
    public void subscribe(Object object) {
        List<Listener> listeners = SUBSCRIPTION_CACHE.computeIfAbsent(object, o ->
                Arrays.stream(o.getClass().getDeclaredFields())
                        .filter(EventManager::isValidField)
                        .map(field -> asListener(o, field))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));

        listeners.forEach(this::subscribe);

        // Invoke child event buses
        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.subscribe(object));
    }

    @Override
    public void subscribe(Object... objects) {
        Arrays.stream(objects).forEach(this::subscribe);
    }

    @Override
    public void subscribe(Iterable<Object> objects) {
        objects.forEach(this::subscribe);
    }

    @Override
    public void unsubscribe(Object object) {
        List<Listener> objectListeners = SUBSCRIPTION_CACHE.get(object);
        if (objectListeners == null)
            return;

        SUBSCRIPTION_MAP.values().forEach(listeners -> listeners.removeIf(objectListeners::contains));

        // Invoke child event buses
        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.unsubscribe(object));
    }

    @Override
    public void unsubscribe(Object... objects) {
        Arrays.stream(objects).forEach(this::unsubscribe);
    }

    @Override
    public void unsubscribe(Iterable<Object> objects) {
        objects.forEach(this::unsubscribe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void post(Object event) {
        List<Listener> listeners = SUBSCRIPTION_MAP.get(event.getClass());
        if (listeners != null)
            listeners.forEach(listener -> listener.invoke(event));

        // Invoke child event buses
        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.post(event));
    }

    @Override
    public void attach(EventBus bus) {
        if (!ATTACHED_BUSES.contains(bus))
            ATTACHED_BUSES.add(bus);
    }

    @Override
    public void detach(EventBus bus) {
        if (ATTACHED_BUSES.contains(bus))
            ATTACHED_BUSES.remove(bus);
    }

    /**
     * Checks if a Field is a valid Event Handler field
     * by checking the field type and presence
     * of the {@code EventHandler} annotation.
     *
     * @see EventHandler
     *
     * @param field Field being checked
     * @return Whether or not the Field is valid
     */
    private static boolean isValidField(Field field) {
        return field.isAnnotationPresent(EventHandler.class) && Listener.class.isAssignableFrom(field.getType());
    }

    /**
     * Creates a listener from the specified object and method.
     * After the listener is created, it is passed to the listener
     * subscription method.
     *
     * @see #subscribe(Listener)
     *
     * @param object Parent object
     * @param field Listener field
     */
    private static Listener asListener(Object object, Field field) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Listener listener = (Listener) field.get(object);
            field.setAccessible(accessible);

            if (listener == null)
                return null;

            if (listener.getPriority() > EventPriority.LOWEST || listener.getPriority() < EventPriority.HIGHEST)
                throw new RuntimeException("Event Priority out of bounds! %s");

            return listener;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Subscribes a Listener to the Subscription Map
     *
     * @param listener The listener being registered
     */
    private void subscribe(Listener listener) {
        List<Listener> listeners = SUBSCRIPTION_MAP.computeIfAbsent(listener.getTarget(), target -> new ArrayList<>());

        int index = 0;
        for (; index < listeners.size(); index++) {
            if (listener.getPriority() < listeners.get(index).getPriority()) {
                break;
            }
        }

        listeners.add(index, listener);
    }
}
