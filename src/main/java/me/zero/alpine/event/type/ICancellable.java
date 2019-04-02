package me.zero.alpine.event.type;

/**
 * A type of event that can be cancelled. The exact effect of cancelling an event
 * is dependent on how it is handled in the call-site itself.
 *
 * @see Cancellable
 *
 * @author Brady
 * @since 9/15/2018
 */
public interface ICancellable {

    /**
     * Cancels the event. The event of this is call-site dependent, documentation
     * of cancellable event implementations should explicitly describe behavior that
     * results from cancelling an event.
     */
    void cancel();

    /**
     * @see #cancel()
     * @return Whether or not the event has been cancelled
     */
    boolean isCancelled();
}
