package me.zero.alpine.bus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of {@link EventManager} that is an {@link AttachableEventBus}.
 *
 * @author Brady
 * @since 9/15/2018
 */
public class AttachableEventManager extends EventManager implements AttachableEventBus {

    /**
     * List of attached event buses.
     */
    protected final List<EventBus> attached = new CopyOnWriteArrayList<>();

    public AttachableEventManager(String name) {
        super(name);
    }

    AttachableEventManager(String name, boolean recursiveDiscovery, boolean superListeners) {
        super(name, recursiveDiscovery, superListeners);
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
