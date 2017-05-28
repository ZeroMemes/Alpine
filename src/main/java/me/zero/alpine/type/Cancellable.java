package me.zero.alpine.type;

/**
 * Type of Event that can be cancelled
 *
 * @author Brady
 * @since 2/10/2017 12:00 PM
 */
public class Cancellable {

    /**
     * Cancelled state
     */
    private boolean cancelled;

    /**
     * Cancels the event, this is handled
     * wherever the event is injected to
     * prevent a task from occuring
     */
    public final void cancel() {
        this.cancelled = true;
    }

    /**
     * @return Whether or not the event is cancelled
     */
    public final boolean isCancelled() {
        return this.cancelled;
    }
}
