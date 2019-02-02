package stixar.util;

public class NullCell
{
    public static <T> Cell<T> getCell()
    {
        return new Cell<T>() {
            public T value() { return null; }
            public boolean isValid() { return false; }
        };
    }
}
