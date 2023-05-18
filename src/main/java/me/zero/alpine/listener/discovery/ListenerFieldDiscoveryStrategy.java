package me.zero.alpine.listener.discovery;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import me.zero.alpine.util.Util;
import net.jodah.typetools.TypeResolver;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Brady
 * @since 3.0.0
 */
enum ListenerFieldDiscoveryStrategy implements ListenerDiscoveryStrategy {
    INSTANCE;

    @Override
    public Stream<ListenerCandidate<?>> findAll(Class<? extends Subscriber> cls) {
        return getListenerFields(cls).map(ListenerFieldDiscoveryStrategy::asListener);
    }

    private static Stream<Field> getListenerFields(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredFields()).filter(ListenerFieldDiscoveryStrategy::isListenerField);
    }

    private static boolean isListenerField(Field field) {
        return field.isAnnotationPresent(Subscribe.class) // The field is allowed to be automatically subscribed
            && field.getType().equals(Listener.class)     // The field is of the correct type
            && !Modifier.isStatic(field.getModifiers());  // The field is an instance member
    }

    @SuppressWarnings("unchecked")
    private static <T> ListenerCandidate<T> asListener(Field field) {
        final Class<?> owner = field.getDeclaringClass();

        if (!(field.getGenericType() instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Listener fields must have a specified type parameter");
        }

        // Resolve the actual target type from the field type parameter
        final Class<T> target = (Class<T>) TypeResolver.resolveRawArgument(field.getGenericType(), Listener.class);
        if (target == TypeResolver.Unknown.class) {
            throw new IllegalArgumentException("Unable to resolve Listener type parameter");
        }

        return instance -> {
            try {
                // Create a lookup in the owner class
                final MethodHandles.Lookup lookup = Util.getLookup().in(owner);
                // Read the field using the trusted lookup
                // (This should avoid setAccessible issues in future Java versions)
                final Listener<T> listener = (Listener<T>) lookup.unreflectGetter(field).invoke(instance);
                listener.setTarget(target);
                return listener;
            } catch (Throwable e) {
                throw new IllegalStateException("Unable to read Listener field", e);
            }
        };
    }
}
