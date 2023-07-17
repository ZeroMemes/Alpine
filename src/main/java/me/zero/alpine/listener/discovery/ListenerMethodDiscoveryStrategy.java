package me.zero.alpine.listener.discovery;

import me.zero.alpine.event.Events;
import me.zero.alpine.exception.ListenerBindException;
import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.exception.ListenerFilterException;
import me.zero.alpine.exception.ListenerMethodException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import me.zero.alpine.util.Util;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Brady
 * @since 3.0.0
 */
enum ListenerMethodDiscoveryStrategy implements ListenerDiscoveryStrategy {
    INSTANCE;

    @Override
    public Stream<ListenerCandidate<?>> findAll(Class<? extends Subscriber> cls) {
        return getListenerMethods(cls).map(ListenerMethodDiscoveryStrategy::asListener);
    }

    private static Stream<Method> getListenerMethods(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredMethods()).filter(ListenerMethodDiscoveryStrategy::isListenerMethod);
    }

    private static boolean isListenerMethod(Method method) {
        return method.isAnnotationPresent(Subscribe.class) // The method is allowed to be automatically subscribed
            && !Modifier.isStatic(method.getModifiers());  // The method is an instance member
    }

    @SuppressWarnings("unchecked")
    private static <T> ListenerCandidate<T> asListener(Method method) {
        final Class<?> owner = method.getDeclaringClass();

        final Type[] parameters = method.getGenericParameterTypes();
        if (parameters.length != 1) {
            throw new ListenerMethodException("Listener methods must have exactly 1 parameter");
        }

        // Validate the event type. If an exception is thrown, wrap it in ListenerDiscoveryException and rethrow it.
        final Class<T> target = (Class<T>) Util.catchAndRethrow(
            () -> Events.validateEventType(parameters[0]),
            cause -> new ListenerDiscoveryException("Couldn't validate event type", cause)
        );

        final Class<? extends Predicate<? super T>>[] filterTypes =
            (Class<? extends Predicate<? super T>>[]) method.getAnnotation(Subscribe.class).filters();

        final Predicate<? super T>[] filters = Arrays.stream(filterTypes).map(type -> {
            final MethodHandles.Lookup lookup = Util.getLookup().in(type);
            final MethodHandle constructor = Util.catchAndRethrow(
                () -> lookup.findConstructor(type, MethodType.methodType(void.class)),
                cause -> new ListenerFilterException("Filter class requires a no-arg constructor", cause)
            );
            return (Predicate<? super T>) Util.catchAndRethrow(
                constructor::invoke,
                cause -> new ListenerFilterException("Unable to construct filter", cause)
            );
        }).toArray(Predicate[]::new);

        final int priority = method.getAnnotation(Subscribe.class).priority();

        // Create a lazily-initialized factory for providing Consumers bound to the target method
        final Callable<MethodHandle> factory = Util.lazy(() -> {
            // Create a lookup in the owner class
            final MethodHandles.Lookup lookup = Util.getLookup().in(owner);
            return LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(Consumer.class, owner),
                MethodType.methodType(Void.TYPE, Object.class),
                lookup.unreflect(method),
                MethodType.methodType(Void.TYPE, target)
            ).getTarget();
        });

        return ListenerCandidate.single(instance -> {
            try {
                // Bind the instance to the event callback method using the factory
                final Consumer<T> callback = (Consumer<T>) factory.call().invoke(instance);

                // TODO: Caching?
                return new Listener<>(target, callback, priority, filters);
            } catch (Throwable e) {
                throw new ListenerBindException("Unable to bind Listener method", e);
            }
        });
    }
}
