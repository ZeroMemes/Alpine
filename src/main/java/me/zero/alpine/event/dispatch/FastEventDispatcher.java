package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @author Brady
 * @since 3.0.0
 */
enum FastEventDispatcher implements EventDispatcher {
    INSTANCE;

    @Override
    public <T> void dispatch(final @NotNull T event, final @NotNull Listener<T>[] listeners) {
        int i = 0;
        while (i != listeners.length) {
            listeners[i].accept(event);
            i++;
        }
    }
}
