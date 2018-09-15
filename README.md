# Alpine
The lightweight event system framework

# Tutorial
For starters, we must create an EventBus to handle event flow.
The Alpine Event Framework provides a default implementation of EventBus, so we'll be using that.
<br><br>
To create a new event bus, instantiate the type ``me.zero.alpine.EventManager`` in some sort of handler file.
```Java
public class Core {
    
    public static final EventBus EVENT_BUS = new EventManager();
}
```
We will make references to this event bus whenever we are in need of carrying out event related tasks.
<br><br>
Now to registering objects to listen to the event bus, we'll need to create a Listener object, and give it some
sort of generic type parameter. This generic type is the type of event we'll be listening for. I will be using
``java.lang.String`` for this example.
```Java
public class EventProcessor implements Listenable {
    
    @EventHandler
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    });
}
```
In order to use our newly created "EventProcessor" class, we need to instantiate it, and then subscribe it to the EventBus.
Active EventBus subscribers get their Listeners invoked whenever a post call is made with the same type as the listener.
Classes containing static listeners may not be subscribed to the EventBus.
```Java
public class Main {
    
    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        Core.EVENT_BUS.subscribe(processor);
        Core.EVENT_BUS.post("Test");
    }
}
```
The code above should give a single line console output of "Test".
<br><br>
Listeners can have filters applied, so that only certain events may be passed to them. Below is an example of a String Filter.
```Java
public class LengthOf3Filter implements Predicate<String> {
    
    @Override
    public boolean test(String t) {
        return t.length() == 3;
    }
}
```
Here, we only accept String input if the length is ``3``. To add our filter to our listener, we just create a new instance of it and add it onto the listener parameters.
```Java
public class EventProcessor {
    
    @EventHandler
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    }, new LengthOf3Filter());
}
```
Now if we run our code, we shouldn't get any console output because the length of "Test" is not equal to ``3``.
Filters that don't have a type matching that of its parent Listener aren't valid.
