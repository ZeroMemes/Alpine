# Alpine
A lightweight event system for Java 8+

# Tutorial
For starters, we must create an EventBus to handle events and their respective listeners.
Alpine provides a default implementation of EventBus that is configurable through a builder, so we'll be using that:
```java
public class MyApplication {

    public static final EventBus EVENT_BUS = EventManager.builder()
        .setName("my_application/root") // Descriptive name for the bus
        .setSuperListeners()            // Enable Listeners to receive subtypes of their target
        .build();
}
```
Specifying a name for the bus is required; although, there is no hard restriction on its uniqueness. Additional settings
such as `setSuperListeners` can be seen in the documentation of [`EventBusBuilder`](src/main/java/me/zero/alpine/bus/EventBusBuilder.java).

Now to actually receive events that are posted to the event bus, we'll need to create a `Listener` object and supply its
generic argument with the type of event we'd like to receive. One of the ways that this can be done by creating a
`Listener` member variable in a class implementing `Subscriber`, and annotating it with `@Subscribe`. Let's do that
in our existing class:
```java
public class MyApplication implements Subscriber {

    public static final EventBus EVENT_BUS = ...;

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    });
}
```
In order to use our `Listener`, we need to create a new instance of the `Subscriber` implementation and subscribe
it to the event bus.
```java
public class MyApplication implements Subscriber {

    public static final EventBus EVENT_BUS = ...;

    public static void main(String[] args) {
        MyApplication app = new MyApplication();
        EVENT_BUS.subscribe(app);
    }

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    });
}
```
An alternative to creating a `Subscriber` implementation and using annotated `Listener` fields to receive events
is creating an independent `Listener` instance and subscribing it directly:
```java
public class MyApplication {

    public static final EventBus EVENT_BUS = ...;
    
    public static void main(String[] args) {
        EVENT_BUS.subscribe(new Listener<String>(str -> {
            System.out.println(str);
        }));
        // or, alternatively... (println has a String implementation which will get bound to here)
        EVENT_BUS.subscribe(new Listener<String>(System.out::println));
    }
}
```
In cases where a method reference (`::`) is used for a `Listener` body and the underlying method's argument isn't the
`Listener` target, a `ClassCastException` can occur during runtime. This is due to incorrect target resolution, and can
be fixed by explicitly specifying the target type:
```java
public class MyApplication {

    public static final EventBus EVENT_BUS = ...;

    public static void main(String[] args) {
        // Incorrect, can cause ClassCastException upon runtime depending on EventBus configuration
        EVENT_BUS.subscribe(new Listener<String>(Main::supertypeAcceptor));

        // Correct, explicitly defines the target and no runtime exception or unintended behavior will occur
        EVENT_BUS.subscribe(new Listener<>(String.class, Main::supertypeAcceptor));
    }

    // Note the 'Object' argument type, this is what causes the need for an explicit target
    private static void supertypeAcceptor(Object o) {
        System.out.println(o);
    }
}
```
Providing our `Listener` with an event, in this case, any `String`, is straight-forward:
```java
public class MyApplication {

    public static final EventBus EVENT_BUS = ...;

    public static void main(String[] args) {
        MyApplication app = new MyApplication();
        EVENT_BUS.subscribe(app);
        EVENT_BUS.post("Test");
    }

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        // Prints "Test"
        System.out.println(str);
    });
}
```
Listeners may have filters applied which must pass in order for the body to receive a given event. A listener can have as
many filters as is needed, which are added as the last arguments in the `Listener` constructor.
```java
public class MyApplication {

    ...

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        // No output, "Test".length() != 3
        System.out.println(str);
    }, new LengthOf3Filter()); // <-- Predicate<? super String>... as last argument to Listener

    // Create nested class implementation of our filter
    public static class LengthOf3Filter implements Predicate<CharSequence> {

        @Override
        public boolean test(CharSequence t) {
            return t.length() == 3;
        }
    }
}
```
The complete example class can be found in [Java](example/src/main/java/JavaApplication.java) and [Kotlin](example/src/main/kotlin/KotlinApplication.kt).
