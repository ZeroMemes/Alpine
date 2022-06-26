package me.zero.alpine.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark {@link Listener} fields to be targeted during an {@link EventSubscriber}'s {@link Listener} field
 * discovery. {@link Listener} fields which are not annotated with {@link Subscribe} will not be registered for
 * invocation by their target event.
 *
 * @author Brady
 * @since 1/21/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subscribe {}
