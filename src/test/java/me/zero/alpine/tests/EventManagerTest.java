package me.zero.alpine.tests;

import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.event.EventPriority;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventManagerTest {

    private static final EventBus bus = new EventManager();

    private static final List<Integer> received = new ArrayList<>();

    @EventHandler
    private static final Listener<TestEvent> listenerA = new Listener<>(e -> received.add(EventPriority.LOWEST), EventPriority.LOWEST);

    @EventHandler
    private static final Listener<TestEvent> listenerB = new Listener<>(e -> received.add(EventPriority.LOW), EventPriority.LOW);

    @EventHandler
    private static final Listener<TestEvent> listenerC1 = new Listener<>(e -> received.add(EventPriority.MEDIUM), EventPriority.MEDIUM);

    @EventHandler
    private static final Listener<TestEvent> listenerC2 = new Listener<>(e -> received.add(EventPriority.MEDIUM), EventPriority.MEDIUM);

    @EventHandler
    private static final Listener<TestEvent> listenerD = new Listener<>(e -> received.add(EventPriority.HIGH), EventPriority.HIGH);

    @EventHandler
    private static final Listener<TestEvent> listenerE = new Listener<>(e -> received.add(EventPriority.HIGHEST), EventPriority.HIGHEST);

    private static final List<Listener<TestEvent>> listeners = new ArrayList<Listener<TestEvent>>() {{
        add(listenerA);
        add(listenerB);
        add(listenerC1);
        add(listenerC2);
        add(listenerD);
        add(listenerE);
    }};

    public void run() {
        Collections.shuffle(listeners);
        listeners.forEach(bus::subscribe);
        bus.post(new TestEvent());
        System.out.println(received.toString());
        assertEquals(received.size(), 6);
        assertEquals(received.get(0), EventPriority.HIGHEST);
        assertEquals(received.get(1), EventPriority.HIGH);
        assertEquals(received.get(2), EventPriority.MEDIUM);
        assertEquals(received.get(3), EventPriority.MEDIUM);
        assertEquals(received.get(4), EventPriority.LOW);
        assertEquals(received.get(5), EventPriority.LOWEST);
    }

    void assertEquals(Object a, Object b) {
        if (!a.equals(b)) {
            System.err.println("test failed!");
            throw new IllegalStateException();
        }
    }

}
