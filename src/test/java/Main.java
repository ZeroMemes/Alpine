public class Main {

    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        Core.EVENT_BUS.subscribe(processor);
        Core.EVENT_BUS.post("Test"); // Shouldn't give console output
        Core.EVENT_BUS.post("123"); // Should give "123" console output
        Core.EVENT_BUS.unsubscribe(processor);
        Core.EVENT_BUS.post("321");
    }
}
