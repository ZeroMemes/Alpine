package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import java.util.function.Consumer;
import java.util.stream.Stream;

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
    void testPropagateThrow() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            // Dispatch the throwing listener
            propagateDispatcher.dispatch(new Object(), new Listener[] { throwingListener });
        });

        // The thrown exception is the same object as the one expected to be thrown
        assertSame(thrownException, thrown);
    }

    @Test
    void testIgnoreThrow() {
        assertDoesNotThrow(() -> {
            // Dispatch the throwing listener
            ignoreDispatcher.dispatch(new Object(), new Listener[] { throwingListener });
        });
    }

    @Nested
    class CallOrder {

        Consumer<Object> before, throwing, after;
        Listener<Object>[] listeners;
        InOrder inOrder;

        @BeforeEach
        void setup() {
            before = mock(Consumer.class);
            throwing = mock(Consumer.class);
            after = mock(Consumer.class);

            doThrow(new RuntimeException()).when(throwing).accept(any());

            // noinspection unchecked
            listeners = (Listener<Object>[]) Stream.of(before, throwing, after)
                .map(cb -> new Listener<>(Object.class, cb))
                .toArray(Listener[]::new);
            inOrder = inOrder(before, throwing, after);
        }

        @Test
        void testPropagate() {
            try {
                propagateDispatcher.dispatch(new Object(), listeners);
            } catch (Exception ignored) {}
        }

        @Test
        void testIgnore() {
            ignoreDispatcher.dispatch(new Object(), listeners);
        }

        @AfterEach
        void verifyOrder() {
            inOrder.verify(before).accept(any());
            inOrder.verify(throwing).accept(any());
            inOrder.verify(after, never()).accept(any());   // 'after' is never called, even if exception is ignored
        }
    }
}
