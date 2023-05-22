package me.zero.alpine.bus;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of {@link EventManager} that is an {@link AttachableEventBus}.
 *
 * @author Brady
 * @since 1.8
 */
public class AttachableEventManager extends EventManager implements AttachableEventBus {

    /**
     * List of attached event buses.
     */
    protected final CopyOnWriteArrayList<EventBus> attached = new CopyOnWriteArrayList<>();

    public AttachableEventManager(@NotNull String name) {
        super(name);
    }

    AttachableEventManager(@NotNull EventBusBuilder<?> builder) {
        super(builder);
    }

    @Override
    public <T> void post(@NotNull T event) {
        super.post(event);
        for (EventBus bus : this.attached) {
            bus.post(event);
        }
    }

    @Override
    public void attach(@NotNull EventBus bus) {
        this.attached.addIfAbsent(bus);
    }

    @Override
    public void detach(@NotNull EventBus bus) {
        this.attached.remove(bus);
    }
}
