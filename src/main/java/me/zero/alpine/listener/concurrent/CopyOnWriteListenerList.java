package me.zero.alpine.listener.concurrent;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Brady
 * @since 3.0.0
 */
public class CopyOnWriteListenerList<T> implements ListenerList<T> {

    protected volatile Listener<T>[] listeners;
    private final Object lock;

    public CopyOnWriteListenerList() {
        this.listeners = newListenerArray(0);
        this.lock = new Object();
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        dispatcher.dispatch(event, this.listeners);
    }

    @Override
    public final boolean add(@NotNull Listener<T> listener) {
        synchronized (this.lock) {
            // TODO: Double-check to avoid always locking
            Listener<T>[] arr = this.listeners;
            if (Arrays.asList(arr).contains(listener)) {
                return false;
            }

            int index = Arrays.binarySearch(arr, listener);
            if (index < 0) {
                index = -index - 1;
            }

            int len = arr.length;
            Listener<T>[] newArr = newListenerArray(len + 1);
            System.arraycopy(arr, 0, newArr, 0, index);
            System.arraycopy(arr, index, newArr, index + 1, len - index);
            newArr[index] = listener;
            this.listeners = newArr;
            return true;
        }
    }

    @Override
    public final boolean remove(@NotNull Listener<T> listener) {
        synchronized (this.lock) {
            // TODO: Double-check to avoid always locking
            Listener<T>[] arr = this.listeners;
            int index = Arrays.asList(arr).indexOf(listener);
            if (index < 0) {
                return false;
            }

            int len = arr.length;
            Listener<T>[] newArr = newListenerArray(len - 1);
            System.arraycopy(arr, 0, newArr, 0, index);
            System.arraycopy(arr, index + 1, newArr, index, len - index - 1);
            this.listeners = newArr;
            return true;
        }
    }

    private static final Listener<?>[] EMPTY_LISTENERS = new Listener<?>[0];

    @SuppressWarnings("unchecked")
    private static <T> Listener<T>[] newListenerArray(int size) {
        return (Listener<T>[]) (size == 0 ? EMPTY_LISTENERS : new Listener[size]);
    }
}
