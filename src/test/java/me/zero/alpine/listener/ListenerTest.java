package me.zero.alpine.listener;

import me.zero.alpine.event.EventPriority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Brady
 */
public class ListenerTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testCompare() {
        final Listener<Object> low = new Listener<>(e -> {}, EventPriority.LOW);
        final Listener<Object> mid = new Listener<>(e -> {}, EventPriority.DEFAULT);
        final Listener<Object> high = new Listener<>(e -> {}, EventPriority.HIGH);

        // Compare with self is 0
        assertEquals(low.compareTo(low), 0);
        assertEquals(mid.compareTo(mid), 0);
        assertEquals(high.compareTo(high), 0);

        // Comparison is only based on priority
        final Listener<Object> duplicate = new Listener<>(e -> {}, high.getPriority());
        assertEquals(high.compareTo(duplicate), 0);
        assertEquals(duplicate.compareTo(high), 0);

        // Lower priorities compare greater
        assertEquals(low.compareTo(mid), 1);
        assertEquals(mid.compareTo(high), 1);

        // Higher priorities compare lesser
        assertEquals(high.compareTo(mid), -1);
        assertEquals(mid.compareTo(low), -1);
    }
}
