package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Brady
 * @since 3.0.0
 */
public final class ListenerArrayList<T> implements ListenerList<T> {

    private final ArrayList<Listener<T>> backing;

    public ListenerArrayList() {
        this.backing = new ArrayList<>();
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        dispatcher.dispatch(event, this.backing.iterator());
    }

    @Override
    public boolean add(@NotNull Listener<T> listener) {
        if (this.backing.contains(listener)) {
            return false;
        }
        return this.backing.add(listener); // returns 'true'
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        return this.backing.remove(listener);
    }
}
