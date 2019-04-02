package me.zero.alpine.listener;

import me.zero.alpine.bus.EventManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark {@link Listener} type fields to be targeted during
 * object listener discovery. {@link Listener} type fields that
 * are unmarked will not be added to the {@code SUBSCRIPTION_CACHE}
 *
 * @see Listener
 * @see EventManager
 *
 * @author Brady
 * @since 1/21/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventHandler {}
