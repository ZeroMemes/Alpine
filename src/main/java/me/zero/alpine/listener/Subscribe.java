package me.zero.alpine.listener;

import me.zero.alpine.bus.EventBus;
import me.zero.alpine.listener.discovery.ListenerDiscoveryStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark instance members of classes implementing {@link Subscriber} as recognizable during listener discovery.
 * <p>
 * When an {@link EventBus} is using the built-in {@code subscribeFields()} discovery strategy, fields annotated with
 * {@link Subscribe} must be of the type {@link Listener} and have an explicit type parameter.
 *
 * <pre>
 * public class EventHandler implements Subscriber {
 *   &#64;Subscribe
 *   public Listener&lt;Event&gt; eventListener = new Listener&lt;&gt;(event -&gt; {
 *     // process 'event'
 *   });
 * }
 * </pre>
 *
 * When an {@link EventBus} is using the built-in {@code subscribeMethods()} discovery strategy, methods annotated with
 * {@link Subscribe} must return {@code void} and have a single parameter which is the target event type.
 *
 * <pre>
 * public class EventHandler implements Subscriber {
 *   &#64;Subscribe
 *   public onEvent(Event event) {
 *     // process 'event'
 *   }
 * }
 * </pre>
 *
 * @see ListenerDiscoveryStrategy#subscribeFields()
 * @see ListenerDiscoveryStrategy#subscribeMethods()
 *
 * @author Brady
 * @since 1.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Subscribe {}
