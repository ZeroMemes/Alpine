package me.zero.alpine.listener.discovery;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscriber;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerCandidate<T> {

    Listener<T> bind(Subscriber instance);
}
