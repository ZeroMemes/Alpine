package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.concurrent.ReadWriteLockListenerList;
import me.zero.alpine.listener.concurrent.SynchronizedListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * A collection of {@link Listener} instances, ordered by {@link Listener#getPriority() priority}. Supports the minimal
 * operations of {@link #add}, {@link #remove}, and posting an event to all listeners via {@link #post}. Thread-safe
 * wrappers over existing {@link ListenerList} instances may be created via the utility methods provided in this
 * interface, or created from one of the implementations in {@link me.zero.alpine.listener.concurrent concurrent}.
 *
 * @author Brady
 * @since 3.0.0
 */
public interface ListenerList<T> {

    /**
     * Posts the specified event to all the {@link Listener}s in this list, using the specified dispatcher.
     *
     * @param event      The event
     * @param dispatcher The event dispatcher
     * @since 3.0.0
     */
    void post(@NotNull T event, @NotNull EventDispatcher dispatcher);

    /**
     * Adds the specified {@link Listener} to this list, if it is not already present.
     *
     * @param listener The listener
     * @return {@code true} if the listener was added
     * @since 3.0.0
     */
    boolean add(@NotNull Listener<T> listener);

    /**
     * Removes the specified {@link Listener} from this list, if it is already present.
     *
     * @param listener The listener
     * @return {@code true} if the listener was removed
     * @since 3.0.0
     */
    boolean remove(@NotNull Listener<T> listener);

    /**
     * Creates a new wrapper around the specified {@link ListenerList} which synchronizes all operations on
     * {@code this}, i.e. the wrapper object itself.
     *
     * @param list A listener list
     * @param <T>  The event type
     * @return A synchronized wrapper of {@code list}
     * @since 3.0.0
     */
    static <T> ListenerList<T> synchronize(@NotNull ListenerList<T> list) {
        return new SynchronizedListenerList<>(list);
    }

    /**
     * Creates a new wrapper around the specified {@link ListenerList} which synchronizes all operations on the
     * specified {@code sync} Object.
     *
     * @param list A listener list
     * @param sync The object to synchronize on
     * @param <T>  The event type
     * @return A synchronized wrapper of {@code list}
     * @since 3.0.0
     */
    static <T> ListenerList<T> synchronize(@NotNull ListenerList<T> list, @NotNull Object sync) {
        return new SynchronizedListenerList<>(list, sync);
    }

    /**
     * Creates a new wrapper around the specified {@link ListenerList} which synchronizes operations using a
     * {@link ReadWriteLock}. The read lock is acquired when {@link ListenerList#post} is called, and the write lock is
     * acquired when {@link ListenerList#add} and {@link ListenerList#remove} are called. This is inherently more
     * complex than a regular mutex, so {@link #synchronize} should perform better on average. This is especially
     * {@code true} if events of the same type aren't posted from multiple threads.
     *
     * @param list A listener list
     * @param <T>  The event type
     * @return A synchronized wrapper of {@code list}
     * @since 3.0.0
     */
    static <T> ListenerList<T> readWriteLock(@NotNull ListenerList<T> list) {
        return new ReadWriteLockListenerList<>(list);
    }
}
