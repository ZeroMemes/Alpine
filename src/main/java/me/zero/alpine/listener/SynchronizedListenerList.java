package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;

/**
 * @author Brady
 * @since 3.0.0
 */
public class SynchronizedListenerList<T> implements ListenerList<T> {

    private final ListenerList<T> backing;
    private final Object sync;

    public SynchronizedListenerList(ListenerList<T> backing) {
        this.backing = backing;
        this.sync = this;
    }

    public SynchronizedListenerList(ListenerList<T> backing, Object sync) {
        this.backing = backing;
        this.sync = sync;
    }

    @Override
    public void post(T event, EventDispatcher dispatcher) {
        synchronized (this.sync) {
            this.backing.post(event, dispatcher);
        }
    }

    @Override
    public boolean add(Listener<T> listener) {
        synchronized (this.sync) {
            return this.backing.add(listener);
        }
    }

    @Override
    public boolean remove(Listener<T> listener) {
        synchronized (this.sync) {
            return this.backing.remove(listener);
        }
    }
}
