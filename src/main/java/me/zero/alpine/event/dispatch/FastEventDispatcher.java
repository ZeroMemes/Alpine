package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Brady
 * @since 3.0.0
 */
enum FastEventDispatcher implements EventDispatcher {
    INSTANCE;

    @Override
    public <T> void dispatch(final @NotNull T event, final @NotNull Iterator<Listener<T>> listeners) {
        while (listeners.hasNext()) {
            listeners.next().accept(event);
        }
    }
}
