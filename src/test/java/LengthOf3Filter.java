import java.util.function.Predicate;

public class LengthOf3Filter implements Predicate<CharSequence> {

    @Override
    public boolean test(CharSequence t) {
        return t.length() == 3;
    }
}