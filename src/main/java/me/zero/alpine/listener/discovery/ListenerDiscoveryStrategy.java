package me.zero.alpine.listener.discovery;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;

import java.util.stream.Stream;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerDiscoveryStrategy {

    Stream<ListenerCandidate<?>> findAll(Class<? extends Subscriber> cls);

    /**
     * @return The built-in discovery strategy for {@link Listener} fields annotated with {@link Subscribe}
     * @since 3.0.0
     */
    static ListenerDiscoveryStrategy subscribeFields() {
        return ListenerFieldDiscoveryStrategy.INSTANCE;
    }

    /**
     * @return The built-in discovery strategy for event callback methods annotated with {@link Subscribe}
     * @since 3.0.0
     */
    static ListenerDiscoveryStrategy subscribeMethods() {
        return ListenerMethodDiscoveryStrategy.INSTANCE;
    }
}
