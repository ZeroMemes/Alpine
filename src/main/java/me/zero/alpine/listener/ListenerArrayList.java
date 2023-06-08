package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An implementation of {@link ListenerList} which is backed by
 *
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
        int index = Collections.binarySearch(this.backing, listener);
        if (index < 0) {
            index = -index - 1;
        }
        this.backing.add(index, listener);
        return true;
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        return this.backing.remove(listener);
    }
}
