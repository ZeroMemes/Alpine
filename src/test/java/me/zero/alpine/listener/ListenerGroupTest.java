package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Brady
 */
public class ListenerGroupTest {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void distributorCallsChildren(int numChildren) {
        final ListenerGroup<Object> root = new ListenerGroup<>((ListenerList<Object>) mock(ListenerList.class));

        final ListenerList<Object>[] backingLists = new ListenerList[numChildren];
        for (int i = 0; i < backingLists.length; i++) {
            final ListenerList<Object> list = (ListenerList<Object>) mock(ListenerList.class);
            final ListenerGroup<Object> group = new ListenerGroup<>(list);
            backingLists[i] = list;
            root.addChild(group);
        }

        final Object event = new Object();
        root.post(event, mock(EventDispatcher.class));

        for (ListenerList<Object> list : backingLists) {
            verify(list).post(eq(event), any());
        }
    }
}
