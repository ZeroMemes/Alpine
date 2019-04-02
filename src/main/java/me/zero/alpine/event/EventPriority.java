package me.zero.alpine.event;

import me.zero.alpine.listener.Listener;

/**
 * Definitions of all of the default priority values. By default, a {@link Listener}
 * will hold the {@link #DEFAULT} priority. Higher event priority, which is reflected
 * by a higher literal integer value, will result in listeners being called first for
 * their respective event post sequence.
 *
 * @author Brady
 * @since 1/21/2017
 */
public interface EventPriority {

    int HIGHEST =  200;
    int HIGH    =  100;
    int MEDIUM  =  0;
    int LOW     = -100;
    int LOWEST  = -200;

    int DEFAULT = MEDIUM;
}
