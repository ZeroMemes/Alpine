package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Brady
 */
@SuppressWarnings("unchecked")
public class ListenerMethodDiscoveryStrategyTest {

    static ListenerDiscoveryStrategy strategy;

    @BeforeAll
    static void setup() {
        strategy = ListenerMethodDiscoveryStrategy.INSTANCE;
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class DiscoverAndInvoke {

        static EventHandler handler;
        static ListenerCandidate<?> candidate;
        static Listener<String> listener;

        @BeforeAll
        static void setup() {
            handler = mock(EventHandler.class);
        }

        @Test
        @Order(1)
        void validMethodIsDiscovered() {
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
        void listenerInvokesMethod() {
            // Listener 'accept' should invoke the callback method
            final String event = "TestStringEvent";
            listener.accept(event);
            verify(handler, times(1)).onEventSubscribed(event);
        }
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void callbackWithMultipleParametersThrows() {
        // Multiple method parameters are disallowed
        class Setup implements Subscriber {
            @Subscribe
            void onEvent(Object a, Object b) {}
        }

        // A terminal operation is required to evaluate the elements
        assertThrows(ListenerDiscoveryException.class, () -> strategy.findAll(Setup.class).count());
    }

    /**
     * Test class for {@link DiscoverAndInvoke}
     */
    static class EventHandler implements Subscriber {

        // ******************
        //    VALID METHODS
        // ******************
        @Subscribe
        void onEventSubscribed(String s) {}

        // ******************
        //   INVALID METHODS
        // ******************
        void onEvent(String s) {}

        @Subscribe
        static void onEventStaticSubscribed(String s) {}

        static void onEventStatic(String s) {}
    }
}
