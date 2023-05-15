package me.zero.alpine.bus;

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
    protected final CopyOnWriteArrayList<EventBus> attached = new CopyOnWriteArrayList<>();

    public AttachableEventManager(String name) {
        super(name);
    }

    AttachableEventManager(EventBusBuilder<?> builder) {
        super(builder);
    }

    @Override
    public <T> void post(T event) {
        super.post(event);
        for (EventBus bus : this.attached) {
            bus.post(event);
        }
    }

    @Override
    public void attach(EventBus bus) {
        this.attached.addIfAbsent(bus);
    }

    @Override
    public void detach(EventBus bus) {
        this.attached.remove(bus);
    }
}
