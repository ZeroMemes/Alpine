package me.zero.alpine.listener.concurrent;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Brady
 * @since 3.0.0
 */
public class ReadWriteLockListenerList<T> implements ListenerList<T> {

    private final ListenerList<T> backing;
    private final ReentrantReadWriteLock.ReadLock r;
    private final ReentrantReadWriteLock.WriteLock w;

    public ReadWriteLockListenerList(ListenerList<T> backing) {
        this.backing = Objects.requireNonNull(backing);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.r = lock.readLock();
        this.w = lock.writeLock();
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        this.r.lock();
        try {
            this.backing.post(event, dispatcher);
        } finally {
            this.r.unlock();
        }
    }

    @Override
    public boolean add(@NotNull Listener<T> listener) {
        this.w.lock();
        try {
            return this.backing.add(listener);
        } finally {
            this.w.unlock();
        }
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        this.w.lock();
        try {
            return this.backing.remove(listener);
        } finally {
            this.w.unlock();
        }
    }
}
