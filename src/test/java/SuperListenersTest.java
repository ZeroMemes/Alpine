import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

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
        final Consumer<Object> callback = mock(Consumer.class);
        final Listener<Object> listener = new Listener<>(Object.class, callback);

        bus.subscribe(listener);
        verify(callback, never()).accept(any());

        final Object event = new Object();
        bus.post(event);
        verify(callback, times(1)).accept(event);
    }

    @Test
    void testSingle() {
        final Consumer<Object> callback = mock(Consumer.class);
        final Listener<Object> listener = new Listener<>(Object.class, callback);

        bus.subscribe(listener);
        verify(callback, never()).accept(any());

        // Class with one parent (Object)
        class Event {}
        final Event event = new Event();

        bus.post(event);
        verify(callback, times(1)).accept(event);
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testMulti() {
        final Consumer<Object> callbackA = mock(Consumer.class);
        final Listener<List> listenerA = new Listener<>(List.class, callbackA::accept);

        final Consumer<Object> callbackB = mock(Consumer.class);
        final Listener<Collection> listenerB = new Listener<>(Collection.class, callbackB::accept);

        bus.subscribe(listenerA);
        bus.subscribe(listenerB);
        verify(callbackA, never()).accept(any());
        verify(callbackB, never()).accept(any());

        // Ensure that the listeners aren't called twice due to 2 inheritance paths
        //  ArrayList -> List
        //  ArrayList -> AbstractList -> List
        final ArrayList list = new ArrayList();
        bus.post(list);
        verify(callbackA, times(1)).accept(list);
        verify(callbackB, times(1)).accept(list);

        // Collection which doesn't implement List
        HashSet set = new HashSet();
        bus.post(set);
        verify(callbackA, never()).accept(set);
        verify(callbackB, times(1)).accept(set);

        // Total # of callback invocations
        verify(callbackA, times(1)).accept(any());
        verify(callbackB, times(2)).accept(any());
    }
}
