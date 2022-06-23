import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;

public class Core {

    public static final EventBus EVENT_BUS = EventManager.builder()
        .setName("root")
        .setRecursiveDiscovery()
        .setSuperListeners()
        .build();
}
