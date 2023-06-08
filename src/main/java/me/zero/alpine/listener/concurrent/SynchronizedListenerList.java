package me.zero.alpine.listener.concurrent;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A wrapper implementation of {@link ListenerList} which uses a mutex object for synchronization.
 *
 * @author Brady
 * @see ListenerList#synchronize
 * @since 3.0.0
 */
public final class SynchronizedListenerList<T> implements ListenerList<T> {

    private final ListenerList<T> backing;
    private final Object sync;

    public SynchronizedListenerList(ListenerList<T> backing) {
        this.backing = Objects.requireNonNull(backing);
        this.sync = this;
    }

    public SynchronizedListenerList(ListenerList<T> backing, Object sync) {
        this.backing = Objects.requireNonNull(backing);
        this.sync = Objects.requireNonNull(sync);
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        synchronized (this.sync) {
            this.backing.post(event, dispatcher);
        }
    }

    @Override
    public boolean add(@NotNull Listener<T> listener) {
        synchronized (this.sync) {
            return this.backing.add(listener);
        }
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        synchronized (this.sync) {
            return this.backing.remove(listener);
        }
    }
}
