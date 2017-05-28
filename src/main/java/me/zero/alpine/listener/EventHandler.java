package me.zero.alpine.listener;

import me.zero.alpine.EventManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark Methods as Event Handlers
 *
 * @see EventManager
 *
 * @author Brady
 * @since 1/21/2017 12:00 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventHandler {}
