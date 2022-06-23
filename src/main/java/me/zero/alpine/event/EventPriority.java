package me.zero.alpine.event;

import me.zero.alpine.listener.Listener;

/**
 * This interface provides predefined literal values for descriptions of various priority levels.
 * <p>
 * As it suggests, a {@link Listener} with an unspecified priority will hold the {@link #DEFAULT} value.
 * {@link Listener}s with a higher literal value for their priority will receive events being posted prior to those
 * with a lower priority value.
 *
 * @author Brady
 * @since 1/21/2017
 */
public interface EventPriority {

    int HIGHEST = Integer.MAX_VALUE;
    int HIGH = 500;
    int MEDIUM = 0;
    int LOW = -500;
    int LOWEST = Integer.MIN_VALUE;

    int DEFAULT = MEDIUM;
}
