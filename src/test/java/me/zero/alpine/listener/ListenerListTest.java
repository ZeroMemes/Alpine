package me.zero.alpine.listener;

import me.zero.alpine.event.EventPriority;
import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.concurrent.CopyOnWriteListenerList;
import me.zero.alpine.listener.concurrent.ReadWriteLockListenerList;
import me.zero.alpine.listener.concurrent.SynchronizedListenerList;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Brady
 */
public interface ListenerListTest<T extends ListenerList<Object>> {

    T create();

    @Test
    default void postCallsListener() {
        final EventDispatcher dispatcher = createMockDispatcher();

        final T list = create();
        final Listener<Object> listener = mock(Listener.class);
        final Object event = new Object();

        list.add(listener);
        list.post(event, dispatcher);
        verify(listener, times(1)).accept(event);
    }

    @Test
    default void addReturnsUpdated() {
        final T list = create();
        final Listener<Object> listener = mock(Listener.class);

        assertTrue(list.add(listener));
        assertFalse(list.add(listener));
    }

    @Test
    default void removeReturnsUpdated() {
        final T list = create();
        final Listener<Object> listener = mock(Listener.class);

        assertFalse(list.remove(listener));
        list.add(listener);
        assertTrue(list.remove(listener));
        assertFalse(list.remove(listener));
    }

    @Test
    default void listenersAreSorted() {
        final EventDispatcher dispatcher = createMockDispatcher();

        final List<Listener<Object>> listeners = Stream.of(
            EventPriority.HIGHEST,
            EventPriority.HIGH,
            EventPriority.MEDIUM,
            EventPriority.LOW,
            EventPriority.LOWEST
        ).map(priority -> spy(new Listener<>(e -> {}, priority))).collect(Collectors.toList());

        final T list = create();

        // Add the listeners to the list in a random order
        final List<Listener<Object>> shuffled = new ArrayList<>(listeners);
        Collections.shuffle(shuffled);
        shuffled.forEach(list::add);

        final InOrder inOrder = inOrder(listeners.toArray());

        final Object event = new Object();
        list.post(event, dispatcher);

        for (Listener<Object> listener : listeners) {
            inOrder.verify(listener, times(1)).accept(event);
        }
    }

    static EventDispatcher createMockDispatcher() {
        final EventDispatcher dispatcher = mock(EventDispatcher.class);
        doAnswer(ctx -> {
            Iterator<Listener<Object>> it = ctx.getArgument(1);
            while (it.hasNext()) {
                it.next().accept(ctx.getArgument(0));
            }
            return null;
        }).when(dispatcher).dispatch(any(), any());
        return dispatcher;
    }

    class ArrayListTest implements ListenerListTest<ListenerArrayList<Object>> {

        @Override
        public ListenerArrayList<Object> create() {
            return new ListenerArrayList<>();
        }
    }

    class CopyOnWriteTest implements ListenerListTest<CopyOnWriteListenerList<Object>> {

        @Override
        public CopyOnWriteListenerList<Object> create() {
            return new CopyOnWriteListenerList<>();
        }
    }

    class ReadWriteTest implements ListenerListTest<ReadWriteLockListenerList<Object>> {

        @Override
        public ReadWriteLockListenerList<Object> create() {
            return new ReadWriteLockListenerList<>(new ListenerArrayList<>());
        }
    }

    class SynchronizedTest implements ListenerListTest<SynchronizedListenerList<Object>> {

        @Override
        public SynchronizedListenerList<Object> create() {
            return new SynchronizedListenerList<>(new ListenerArrayList<>());
        }
    }
}
