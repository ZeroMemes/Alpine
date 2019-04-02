package me.zero.alpine.listener;

import me.zero.alpine.bus.EventBus;

/**
 * An interface that must be implemented by a class in order for it to
 * be subscribed to an {@link EventBus}. It does not require any methods
 * to be implemented, the only purpose is to make types containing listeners
 * explicit.
 *
 *
 * @author Brady
 * @since 9/15/2018
 */
public interface Listenable {}
