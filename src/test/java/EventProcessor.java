import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class EventProcessor {

    @EventHandler
    private Listener<String> stringListener = new Listener<>(str -> {
        System.out.println(str);
    }, new LengthOf3Filter());
}
