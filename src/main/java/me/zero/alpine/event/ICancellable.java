package me.zero.alpine.event;

/**
 * A type of event that can be cancelled
 *
 * @see Cancellable
 *
 * @author Brady
 * @since 9/15/2018
 */
public interface ICancellable {

    /**
     * Cancels the event, this is handled wherever the
     * event is invoked to prevent a task from occurring
     */
    void cancel();

    /**
     * @return Whether or not the event is cancelled
     */
    boolean isCancelled();
}
