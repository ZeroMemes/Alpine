package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerDiscoveryStrategy {

    /**
     * Locates all the listener candidates that are provided by the specified {@link Subscriber} class, according to
     * this strategy.
     *
     * @param cls The class to search
     * @return A stream of candidates
     * @throws ListenerDiscoveryException If an error is detected in Listener definition
     * @since 3.0.0
     */
    Stream<ListenerCandidate<?>> findAll(Class<? extends Subscriber> cls);

    /**
     * @return The built-in discovery strategy for {@link Listener} fields annotated with {@link Subscribe}
     * @since 3.0.0
     */
    static @NotNull ListenerDiscoveryStrategy subscribeFields() {
        return ListenerFieldDiscoveryStrategy.INSTANCE;
    }

    /**
     * @return The built-in discovery strategy for event callback methods annotated with {@link Subscribe}
     * @since 3.0.0
     */
    static @NotNull ListenerDiscoveryStrategy subscribeMethods() {
        return ListenerMethodDiscoveryStrategy.INSTANCE;
    }
}
