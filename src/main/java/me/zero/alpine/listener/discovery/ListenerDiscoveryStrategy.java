package me.zero.alpine.listener.discovery;

import me.zero.alpine.listener.Subscriber;

import java.util.stream.Stream;

/**
 * @author Brady
 * @since 5/17/2023
 */
@FunctionalInterface
public interface ListenerDiscoveryStrategy {

    Stream<ListenerCandidate<?>> findAll(Class<? extends Subscriber> cls);
}
