package me.zero.alpine.event;

import me.zero.alpine.listener.Listener;

/**
 * A type of event that can be cancelled. The exact effect of cancelling an event is dependent on how it is handled
 * in the call-site itself. Cancelling an event will not prevent it from being passed to its subscribed
 * {@link Listener}s.
 *
 * @author Brady
 * @see CancellableEvent
 * @since 1.8
 */
public interface Cancellable {

    /**
     * Cancels this event. Equivalent to {@code setCancelled(true)}.
     */
    default void cancel() {
        this.setCancelled(true);
    }

    /**
     * Sets the cancelled state of this event
     *
     * @param cancel The new state
     */
    void setCancelled(boolean cancel);

    /**
     * Returns whether the event has been cancelled
     */
    boolean isCancelled();
}
