package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Brady
 * @since 3.0.0
 */
public final class ListenerGroup<T> implements ListenerList<T> {

    private final ListenerList<T> backing;
    private final List<ListenerGroup<? super T>> children;
    private BiConsumer<T, EventDispatcher> distributor;

    public ListenerGroup(@NotNull ListenerList<T> backing) {
        this.backing = Objects.requireNonNull(backing);
        this.children = new ArrayList<>();
        this.distributor = this.createDistributor();
    }

    @Override
    public void post(@NotNull T event, @NotNull EventDispatcher dispatcher) {
        this.distributor.accept(event, dispatcher);
    }

    @Override
    public boolean add(@NotNull Listener<T> listener) {
        return this.backing.add(listener);
    }

    @Override
    public boolean remove(@NotNull Listener<T> listener) {
        return this.backing.remove(listener);
    }

    private void post0(T event, EventDispatcher dispatcher) {
        this.backing.post(event, dispatcher);
    }

    public void addChild(ListenerGroup<? super T> child) {
        this.children.add(child);
        this.distributor = this.createDistributor();
    }

    private BiConsumer<T, EventDispatcher> createDistributor() {
        switch (this.children.size()) {
            case 0: {
                return this::post0;
            }
            case 1: {
                final ListenerGroup<? super T> g0 = this.children.get(0);
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    g0.post0(event, dispatcher);
                };
            }
            case 2: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1);
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    g0.post0(event, dispatcher);
                    g1.post0(event, dispatcher);
                };
            }
            case 3: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2);
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    g0.post0(event, dispatcher);
                    g1.post0(event, dispatcher);
                    g2.post0(event, dispatcher);
                };
            }
            case 4: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2), g3 = this.children.get(3);
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    g0.post0(event, dispatcher);
                    g1.post0(event, dispatcher);
                    g2.post0(event, dispatcher);
                    g3.post0(event, dispatcher);
                };
            }
            case 5: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2), g3 = this.children.get(3), g4 = this.children.get(4);
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    g0.post0(event, dispatcher);
                    g1.post0(event, dispatcher);
                    g2.post0(event, dispatcher);
                    g3.post0(event, dispatcher);
                    g4.post0(event, dispatcher);
                };
            }
            default: {
                return (event, dispatcher) -> {
                    this.post0(event, dispatcher);
                    for (ListenerGroup<? super T> child : this.children) {
                        child.post0(event, dispatcher);
                    }
                };
            }
        }
    }
}
