# Alpine
A lightweight event system for Java 8+

# Tutorial
For starters, we must create an EventBus to handle events and their respective listeners.
Alpine provides a default implementation of EventBus, so we'll be using that.
<br><br>
Create a new instance of `me.zero.alpine.bus.EventManager` in some sort of handler file.
```java
public class Core {

    public static final EventBus EVENT_BUS = new EventManager("root");
}
```
Now to register objects to listen to the event bus, we'll need to create a Listener object, and give it some
sort of generic type parameter. This generic type is the type of event we'll be listening for. I will be using
`java.lang.String` for this example.
```java
public class EventProcessor implements EventSubscriber {

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    });
}
```
In order to use our newly created `EventProcessor` class we need to create a new instance of it to subscribe to the EventBus.
Active EventBus subscribers get their Listeners invoked whenever a post call is made with the same type as the listener.
```java
public class Main {

    public static void main(String[] args) {
        Core.EVENT_BUS.subscribe(new EventProcessor());
        Core.EVENT_BUS.post("Test");
    }
}
```
The code above should give a single line console output of "Test".
<br><br>
Listeners can have filters applied, so that only certain events may be passed to them. Below is an example of a String Filter.
```java
public class LengthOf3Filter implements Predicate<String> {

    @Override
    public boolean test(String t) {
        return t.length() == 3;
    }
}
```
Here, we only accept String input if the length is ``3``. To add our filter to our listener, we just create a new instance of it and add it onto the listener parameters.
```java
public class EventProcessor {

    @Subscribe
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    }, new LengthOf3Filter());
}
```
Now if we run our code, we shouldn't get any console output because the length of "Test" is not equal to ``3``.
