import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;

public class EventProcessor implements EventSubscriber {

    @Subscribe
    private Listener<String> stringListener = new Listener<>(s -> {
        System.out.println(s);
    }, new LengthOf3Filter());

    @Subscribe
    private Listener<CharSequence> charSequenceListener = new Listener<>(s -> {
        System.out.println(s);
    });
}
