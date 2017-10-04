package me.zero.alpine.type;

/**
 * Contains all of the default {@code Listener} Priorities.
 * Priorities are used to define the order in which a {@code Listener}
 * will be called, relative to other listeners. By default, a Listener
 * will have the {@code MEDIUM} priority level.
 *
 * @author Brady
 * @since 1/21/2017 12:00 PM
 */
public interface EventPriority {

    byte HIGHEST = 1, HIGH = 2, MEDIUM = 3, LOW = 4, LOWEST = 5, DEFAULT = MEDIUM;
}
