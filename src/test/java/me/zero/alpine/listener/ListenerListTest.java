package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.concurrent.CopyOnWriteListenerList;
import me.zero.alpine.listener.concurrent.ReadWriteLockListenerList;
import me.zero.alpine.listener.concurrent.SynchronizedListenerList;
import org.junit.jupiter.api.Test;

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
    default void testPost() {
        T list = create();

        Listener<Object> listener = mock(Listener.class);
        EventDispatcher dispatcher = mock(EventDispatcher.class);
        doAnswer(ctx -> {
            listener.accept(ctx.getArgument(0));
            return null;
        }).when(dispatcher).dispatch(any(), any());

        Object event = new Object();

        list.add(listener);
        list.post(event, dispatcher);
        verify(listener, times(1)).accept(event);
    }

    @Test
    default void testAdd() {
        T list = create();
        Listener<Object> listener = mock(Listener.class);

        assertTrue(list.add(listener));
        assertFalse(list.add(listener));
    }

    @Test
    default void testRemove() {
        T list = create();
        Listener<Object> listener = mock(Listener.class);

        assertFalse(list.remove(listener));
        list.add(listener);
        assertTrue(list.remove(listener));
        assertFalse(list.remove(listener));
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
