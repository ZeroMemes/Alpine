package me.zero.alpine.event;

/**
 * Default implementation of {@link Cancellable}.
 *
 * @author Brady
 * @since 1.2
 */
public class CancellableEvent implements Cancellable {

    private boolean cancelled;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
