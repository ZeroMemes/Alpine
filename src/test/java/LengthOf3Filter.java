import java.util.function.Predicate;

public class LengthOf3Filter implements Predicate<String> {

    @Override
    public boolean test(String t) {
        return t.length() == 3;
    }
}