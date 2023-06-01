package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerBindException;
import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import org.junit.jupiter.api.*;

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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class DiscoverAndInvoke {

        static EventHandler handler;
        static ListenerCandidate<?> candidate;
        static Listener<String> listener;

        @BeforeAll
        static void setup() {
            handler = new EventHandler();
        }

        @Test
        @Order(1)
        void validFieldIsDiscovered() {
            final List<ListenerCandidate<?>> candidates = strategy.findAll(EventHandler.class).collect(Collectors.toList());
            assertEquals(1, candidates.size());
            candidate = candidates.get(0);
        }

        @Test
        @Order(2)
        void bindCandidateToInstance() {
            final List<Listener<String>> listeners = candidate.bind(handler).map(l -> (Listener<String>) l).collect(Collectors.toList());
            assertEquals(1, listeners.size());
            listener = listeners.get(0);
        }

        @Test
        @Order(3)
        void listenerMatchesField() {
            assertEquals(handler.subscribedListener, listener);
        }
    }

    @Test
    @SuppressWarnings({"ResultOfMethodCallIgnored", "rawtypes"})
    void missingTypeParameterThrows() {
        // Absence of a type parameter is disallowed
        class Setup implements Subscriber {
            @Subscribe
            final Listener subscribedListener = new Listener<>(o -> {});
        }

        // A terminal operation is required to evaluate the elements
        assertThrows(ListenerDiscoveryException.class, () -> strategy.findAll(Setup.class).count());
    }

    @Test
    void nullListenerFieldThrows() {
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
     * Test class for {@link DiscoverAndInvoke}
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
