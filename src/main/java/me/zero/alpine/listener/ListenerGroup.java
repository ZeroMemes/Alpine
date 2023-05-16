package me.zero.alpine.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Brady
 * @since 5/10/2023
 */
public final class ListenerGroup<T> {

    private Listener<T>[] listeners;
    private final Object lock;

    private final ListenerExceptionHandler exceptionHandler;
    private final List<ListenerGroup<? super T>> children;
    private Consumer<T> dispatcher;

    public ListenerGroup(ListenerExceptionHandler exceptionHandler) {
        this.listeners = newListenerArray(0);
        this.lock = new Object();
        this.children = new ArrayList<>();
        this.exceptionHandler = exceptionHandler;
        this.dispatcher = this.createDispatcher();
    }

    public void post(T event) {
        this.dispatcher.accept(event);
    }

    private void dispatch(T event) {
        final Listener<T>[] arr = this.listeners;
        int i = 0;
        try {
            while (i != arr.length) {
                arr[i].accept(event);
                i++;
            }
        } catch (Throwable cause) {
            if (this.exceptionHandler.handleException(event, arr[i], cause)) {
                throw cause;
            }
        }
    }

    public void add(Listener<T> listener) {
        synchronized (this.lock) {
            // TODO: Double-check to avoid always locking
            Listener<T>[] arr = this.listeners;
            if (Arrays.asList(arr).contains(listener)) {
                return;
            }

            int index = Arrays.binarySearch(arr, listener);
            if (index < 0) {
                index = -index - 1;
            }

            int len = arr.length;
            Listener<T>[] newArr = newListenerArray(len + 1);
            System.arraycopy(arr, 0, newArr, 0, index);
            System.arraycopy(arr, index, newArr, index + 1, len - index);
            newArr[index] = listener;
            this.listeners = newArr;
        }
    }

    public void remove(Listener<?> listener) {
        synchronized (this.lock) {
            // TODO: Double-check to avoid always locking
            Listener<T>[] arr = this.listeners;
            int index = Arrays.asList(arr).indexOf(listener);
            if (index < 0) {
                return;
            }

            int len = arr.length;
            Listener<T>[] newArr = newListenerArray(len - 1);
            System.arraycopy(arr, 0, newArr, 0, index);
            System.arraycopy(arr, index + 1, newArr, index, len - index - 1);
            this.listeners = newArr;
        }
    }

    public void addChild(ListenerGroup<? super T> child) {
        this.children.add(child);
        this.dispatcher = this.createDispatcher();
    }

    private Consumer<T> createDispatcher() {
        switch (this.children.size()) {
            case 0: {
                return this::dispatch;
            }
            case 1: {
                final ListenerGroup<? super T> g0 = this.children.get(0);
                return event -> {
                    this.dispatch(event);
                    g0.dispatch(event);
                };
            }
            case 2: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1);
                return event -> {
                    this.dispatch(event);
                    g0.dispatch(event);
                    g1.dispatch(event);
                };
            }
            case 3: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2);
                return event -> {
                    this.dispatch(event);
                    g0.dispatch(event);
                    g1.dispatch(event);
                    g2.dispatch(event);
                };
            }
            case 4: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2), g3 = this.children.get(3);
                return event -> {
                    this.dispatch(event);
                    g0.dispatch(event);
                    g1.dispatch(event);
                    g2.dispatch(event);
                    g3.dispatch(event);
                };
            }
            case 5: {
                final ListenerGroup<? super T> g0 = this.children.get(0), g1 = this.children.get(1), g2 = this.children.get(2), g3 = this.children.get(3), g4 = this.children.get(4);
                return event -> {
                    this.dispatch(event);
                    g0.dispatch(event);
                    g1.dispatch(event);
                    g2.dispatch(event);
                    g3.dispatch(event);
                    g4.dispatch(event);
                };
            }
            default: {
                return event -> {
                    this.dispatch(event);
                    for (ListenerGroup<? super T> child : this.children) {
                        child.dispatch(event);
                    }
                };
            }
        }
    }

    private static final Listener<?>[] EMPTY_LISTENERS = new Listener<?>[0];

    @SuppressWarnings("unchecked")
    private static <T> Listener<T>[] newListenerArray(int size) {
        return (Listener<T>[]) (size == 0 ? EMPTY_LISTENERS : new Listener[size]);
    }
}
