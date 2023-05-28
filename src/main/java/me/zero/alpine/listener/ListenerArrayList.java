package me.zero.alpine.listener;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.zero.alpine.event.dispatch.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Brady
 * @since 3.0.0
 */
public final class ListenerArrayList<T> implements ListenerList<T> {

    private final ObjectArraySet<Listener<T>> backing;

    public ListenerArrayList() {
        this.backing = new ObjectArraySet<>();
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        dispatcher.dispatch(event, this.backing.iterator());
    }

    @Override
    public boolean add(@NotNull Listener<T> listener) {
        return this.backing.add(listener);
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        return this.backing.remove(listener);
    }
}
