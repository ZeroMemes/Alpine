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
        assertEquals(0, low.compareTo(low));
        assertEquals(0, mid.compareTo(mid));
        assertEquals(0, high.compareTo(high));

        // Comparison is only based on priority
        final Listener<Object> duplicate = new Listener<>(e -> {}, high.getPriority());
        assertEquals(0, high.compareTo(duplicate));
        assertEquals(0, duplicate.compareTo(high));

        // Lower priorities compare greater
        assertEquals(1, low.compareTo(mid));
        assertEquals(1, mid.compareTo(high));

        // Higher priorities compare lesser
        assertEquals(-1, high.compareTo(mid));
        assertEquals(-1, mid.compareTo(low));
    }
}
