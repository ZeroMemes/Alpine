package me.zero.alpine.bus;

import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link EventManager} that is an {@link AttachableEventBus}
 *
 * @author Brady
 * @since 9/15/2018
 */
public class AttachableEventManager extends EventManager implements AttachableEventBus {

    /**
     * List of attached event buses
     */
    protected final List<EventBus> attached = new ArrayList<>();

    public AttachableEventManager(String name) {
        super(name);
    }

    AttachableEventManager(String name, boolean recursiveDiscovery, boolean superListeners) {
        super(name, recursiveDiscovery, superListeners);
    }

    @Override
    public void subscribe(EventSubscriber subscriber) {
        super.subscribe(subscriber);
        this.attached.forEach(bus -> bus.subscribe(subscriber));
    }

    @Override
    public void subscribe(Listener<?> listener) {
        super.subscribe(listener);
        this.attached.forEach(bus -> bus.subscribe(listener));
    }

    @Override
    public void unsubscribe(EventSubscriber subscriber) {
        super.unsubscribe(subscriber);
        this.attached.forEach(bus -> bus.unsubscribe(subscriber));
    }

    @Override
    public void unsubscribe(Listener<?> listener) {
        super.unsubscribe(listener);
        this.attached.forEach(bus -> bus.unsubscribe(listener));
    }

    @Override
    public void post(Object event) {
        super.post(event);
        this.attached.forEach(bus -> bus.post(event));
    }

    @Override
    public void attach(EventBus bus) {
        if (!this.attached.contains(bus))
            this.attached.add(bus);
    }

    @Override
    public void detach(EventBus bus) {
        this.attached.remove(bus);
    }
}
