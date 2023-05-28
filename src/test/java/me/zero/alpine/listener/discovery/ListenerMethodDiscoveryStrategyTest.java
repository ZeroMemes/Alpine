package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerDiscoveryException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    @Test
    void testDiscovery() {
        final EventHandler handler = mock(EventHandler.class);

        final List<ListenerCandidate<?>> candidates = strategy.findAll(EventHandler.class).collect(Collectors.toList());
        assertEquals(1, candidates.size());

        final List<Listener<String>> listeners = candidates.get(0).bind(handler).map(l -> (Listener<String>) l).collect(Collectors.toList());
        assertEquals(1, listeners.size());

        // Listener 'accept' should invoke the callback method
        final String event = "TestStringEvent";
        listeners.get(0).accept(event);
        verify(handler, times(0)).onEvent(event);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testMultipleParameter() {
        // Multiple method parameters are disallowed
        class Setup implements Subscriber {
            @Subscribe
            void onEvent(Object a, Object b) {}
        }

        // A terminal operation is required to evaluate the elements
        assertThrows(ListenerDiscoveryException.class, () -> strategy.findAll(Setup.class).count());
    }

    /**
     * Test class for {@link #testDiscovery()}
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
