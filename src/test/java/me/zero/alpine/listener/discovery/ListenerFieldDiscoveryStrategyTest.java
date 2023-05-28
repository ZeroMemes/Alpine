package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerBindException;
import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Brady
 */
@SuppressWarnings("unchecked")
public class ListenerFieldDiscoveryStrategyTest {

    static ListenerDiscoveryStrategy strategy;

    @BeforeAll
    static void setup() {
        strategy = ListenerFieldDiscoveryStrategy.INSTANCE;
    }

    @Test
    void testDiscovery() {
        final EventHandler handler = new EventHandler();

        final List<ListenerCandidate<?>> candidates = strategy.findAll(EventHandler.class).collect(Collectors.toList());
        assertEquals(candidates.size(), 1);

        final List<Listener<String>> listeners = candidates.get(0).bind(handler).map(l -> (Listener<String>) l).collect(Collectors.toList());
        assertEquals(listeners.size(), 1);
        assertEquals(listeners.get(0), handler.subscribedListener);
    }

    @Test
    @SuppressWarnings({"ResultOfMethodCallIgnored", "rawtypes"})
    void testMissingParameter() {
        // Absence of a type parameter is disallowed
        class Setup implements Subscriber {
            @Subscribe
            final Listener subscribedListener = new Listener<>(o -> {});
        }

        // A terminal operation is required to evaluate the elements
        assertThrows(ListenerDiscoveryException.class, () -> strategy.findAll(Setup.class).count());
    }

    @Test
    void testNullField() {
        // Listener must be initialized to bind
        class Setup implements Subscriber {
            @Subscribe
            final Listener<String> subscribedListener = null;
        }

        // Other tests validate this behavior, exception should never get thrown
        final ListenerCandidate<?> candidate = strategy.findAll(Setup.class).findFirst()
            .orElseThrow(() -> new IllegalStateException("Listener not found"));

        final Setup instance = new Setup();

        // Attempting to bind should throw a ListenerBindException whose cause is an NPE
        final ListenerBindException ex = assertThrows(ListenerBindException.class, () -> candidate.bind(instance));
        assertInstanceOf(NullPointerException.class, ex.getCause());
    }

    /**
     * Test class for {@link #testDiscovery()}
     */
    static class EventHandler implements Subscriber {

        // ******************
        //    VALID FIELDS
        // ******************
        @Subscribe
        final Listener<String> subscribedListener = new Listener<>(String.class, s -> {});

        // ******************
        //   INVALID FIELDS
        // ******************
        final Listener<String> fieldListener = new Listener<>(s -> {});

        @Subscribe
        static final Listener<String> staticSubscribedListener = new Listener<>(s -> {});

        static final Listener<String> staticFieldListener = new Listener<>(s -> {});

        @Subscribe
        final Object subscribedNonListener = new Object();

        @Subscribe
        static final Object staticSubscribedNonListener = new Object();

        final Object fieldNonListener = new Object();

        static final Object staticFieldNonListener = new Object();
    }
}
