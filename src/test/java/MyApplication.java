import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;

import java.util.function.Predicate;

public class MyApplication implements EventSubscriber {

    public static final EventBus EVENT_BUS = EventManager.builder()
        .setName("my_application/root")
        .setRecursiveDiscovery()
        .setSuperListeners()
        .build();

    public static void main(String[] args) {
        MyApplication app = new MyApplication();
        MyApplication.EVENT_BUS.subscribe(app);
        MyApplication.EVENT_BUS.post("Test");
    }

    @Subscribe
    private Listener<String> stringListener = new Listener<>(s -> {
        System.out.println("stringListener:" + s);
    }, new LengthOf3Filter());

    public static class LengthOf3Filter implements Predicate<CharSequence> {

        @Override
        public boolean test(CharSequence t) {
            return t.length() == 3;
        }
    }
}
