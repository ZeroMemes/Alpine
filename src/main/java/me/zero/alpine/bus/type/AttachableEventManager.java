package me.zero.alpine.bus.type;

import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listenable;
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
     * Holds the list of attached event buses
     */
    private final List<EventBus> attached = new ArrayList<>();

    @Override
    public void subscribe(Listenable listenable) {
        super.subscribe(listenable);

        if (!this.attached.isEmpty())
            this.attached.forEach(bus -> bus.subscribe(listenable));
    }

    @Override
    public void subscribe(Listener listener) {
        super.subscribe(listener);

        if (!this.attached.isEmpty())
            this.attached.forEach(bus -> bus.subscribe(listener));
    }

    @Override
    public void unsubscribe(Listenable listenable) {
        super.unsubscribe(listenable);

        if (!this.attached.isEmpty())
            this.attached.forEach(bus -> bus.unsubscribe(listenable));
    }

    @Override
    public void unsubscribe(Listener listener) {
        super.unsubscribe(listener);

        if (!this.attached.isEmpty())
            this.attached.forEach(bus -> bus.unsubscribe(listener));
    }

    @Override
    public void post(Object event) {
        super.post(event);

        if (!this.attached.isEmpty())
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
