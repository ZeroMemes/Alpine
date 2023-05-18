package me.zero.alpine.listener;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerListFactory {

    <T> ListenerList<T> create(Class<T> cls);
}
