package me.zero.alpine.bus;

import me.zero.alpine.listener.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
        verify(listener, never()).accept(any());

        final Object event = new Object();
        bus.post(event);
        verify(listener, times(1)).accept(event);
    }

    @Test
    void testSingle() {
        final Listener<Object> listener = mock(Listener.class);
        when(listener.getTarget()).thenReturn(Object.class);

        bus.subscribe(listener);
        verify(listener, never()).accept(any());

        // Class with one parent (Object)
        class Event {}
        final Event event = new Event();

        bus.post(event);
        verify(listener, times(1)).accept(event);
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testMulti() {
        final Listener listenerA = mock(Listener.class);
        when(listenerA.getTarget()).thenReturn(List.class);

        final Listener listenerB = mock(Listener.class);
        when(listenerB.getTarget()).thenReturn(Collection.class);

        bus.subscribe(listenerA);
        bus.subscribe(listenerB);
        verify(listenerA, never()).accept(any());
        verify(listenerB, never()).accept(any());

        // Ensure that the listeners aren't called twice due to 2 inheritance paths
        //  ArrayList -> List
        //  ArrayList -> AbstractList -> List
        final ArrayList list = new ArrayList();
        bus.post(list);
        verify(listenerA, times(1)).accept(list);
        verify(listenerB, times(1)).accept(list);

        // Collection which doesn't implement List
        HashSet set = new HashSet();
        bus.post(set);
        verify(listenerA, never()).accept(set);
        verify(listenerB, times(1)).accept(set);

        // Total # of callback invocations
        verify(listenerA, times(1)).accept(any());
        verify(listenerB, times(2)).accept(any());
    }
}
