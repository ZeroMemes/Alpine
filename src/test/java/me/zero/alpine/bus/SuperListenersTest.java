package me.zero.alpine.bus;

import me.zero.alpine.listener.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Brady
 */
@SuppressWarnings("unchecked")
public class SuperListenersTest {

    EventBus bus;

    @BeforeEach
    void setup() {
        bus = EventManager.builder()
            .setName("test")
            .setSuperListeners()
            .build();
    }

    @Test
    void testNone() {
        final Listener<Object> listener = mock(Listener.class);
        when(listener.getTarget()).thenReturn(Object.class);
        bus.subscribe(listener);

        final Object event = new Object();
        bus.post(event);
        verify(listener, times(1)).accept(event);
    }

    @Test
    void testSingle() {
        final Listener<Object> listener = mock(Listener.class);
        when(listener.getTarget()).thenReturn(Object.class);
        bus.subscribe(listener);

        // Class with one parent (Object)
        class Event {}
        final Event event = new Event();

        bus.post(event);
        verify(listener, times(1)).accept(event);
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testDiamond() {
        final Listener listener = mock(Listener.class);
        when(listener.getTarget()).thenReturn(Diamond.D.class);
        bus.subscribe(listener);

        // Ensure that the listener is called once despite 2 inheritance paths
        //  A -> B -> D
        //  A -> C -> D
        final Diamond.A event = new Diamond.A();
        bus.post(event);
        verify(listener, times(1)).accept(event);
    }

    static class Diamond {
        //     A
        //    / \
        //   /   \
        //  B     C
        //   \   /
        //    \ /
        //     D
        static class A implements B, C {}
        // Local interfaces not supported until Java 16
        interface B extends D {}
        interface C extends D {}
        interface D {}
    }
}
