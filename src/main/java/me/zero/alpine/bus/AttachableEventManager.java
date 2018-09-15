package me.zero.alpine.bus;

import me.zero.alpine.bus.type.AttachableEventBus;
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
    private final List<EventBus> ATTACHED_BUSES = new ArrayList<>();

    @Override
    public void subscribe(Listenable listenable) {
        super.subscribe(listenable);

        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.subscribe(listenable));
    }

    @Override
    public void subscribe(Listener listener) {
        super.subscribe(listener);

        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.subscribe(listener));
    }

    @Override
    public void unsubscribe(Listenable listenable) {
        super.unsubscribe(listenable);

        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.unsubscribe(listenable));
    }

    @Override
    public void unsubscribe(Listener listener) {
        super.unsubscribe(listener);

        if (!ATTACHED_BUSES.isEmpty())
            ATTACHED_BUSES.forEach(bus -> bus.unsubscribe(listener));
    }

    @Override
    public void post(Object event) {
        super.post(event);

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
        ATTACHED_BUSES.remove(bus);
    }
}
