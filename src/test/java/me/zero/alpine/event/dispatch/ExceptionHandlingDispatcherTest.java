package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;
import me.zero.alpine.util.Util;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Brady
 */
public class ExceptionHandlingDispatcherTest {

    static EventDispatcher propagateDispatcher, ignoreDispatcher;
    static RuntimeException thrownException;
    static Listener<Object> throwingListener;

    @BeforeAll
    static void setup() {
        // Create an exception handler that always propagates
        ListenerExceptionHandler propagateHandler = mock(ListenerExceptionHandler.class);
        when(propagateHandler.handleException(any(), any(), any())).thenReturn(true);
        propagateDispatcher = EventDispatcher.withExceptionHandler(propagateHandler);

        // Create an exception handler that never propagates
        ListenerExceptionHandler ignoreHandler = mock(ListenerExceptionHandler.class);
        when(ignoreHandler.handleException(any(), any(), any())).thenReturn(false);
        ignoreDispatcher = EventDispatcher.withExceptionHandler(ignoreHandler);

        // Create a Listener that always throws an exception
        thrownException = new RuntimeException();
        throwingListener = new Listener<>(Object.class, e -> { throw thrownException; });
    }

    @Test
    void propagatingHandlerThrows() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            // Dispatch the throwing listener
            propagateDispatcher.dispatch(new Object(), Util.singletonIterator(throwingListener));
        });

        // The thrown exception is the same object as the one expected to be thrown
        assertSame(thrownException, thrown);
    }

    @Test
    void ignoringHandlerDoesNotThrow() {
        assertDoesNotThrow(() -> {
            // Dispatch the throwing listener
            ignoreDispatcher.dispatch(new Object(), Util.singletonIterator(throwingListener));
        });
    }

    @Nested
    class DispatchCancel {

        Listener<Object> before, throwing, after;
        Listener<Object>[] listeners;
        InOrder inOrder;

        @SuppressWarnings({"unchecked"})
        @BeforeEach
        void setup() {
            before = mock(Listener.class);
            throwing = mock(Listener.class);
            after = mock(Listener.class);

            doThrow(new RuntimeException()).when(throwing).accept(any());

            listeners = new Listener[] { before, throwing, after };
            inOrder = inOrder(before, throwing, after);
        }

        @Test
        void propagatingHandlerCancels() {
            try {
                propagateDispatcher.dispatch(new Object(), Util.arrayIterator(listeners));
            } catch (Exception ignored) {}
        }

        @Test
        void ignoringHandlerCancels() {
            ignoreDispatcher.dispatch(new Object(), Util.arrayIterator(listeners));
        }

        @AfterEach
        void verifyOrder() {
            inOrder.verify(before).accept(any());
            inOrder.verify(throwing).accept(any());
            inOrder.verify(after, never()).accept(any());   // 'after' is never called, even if exception is ignored
        }
    }
}
