package stixar.util;

/**
   Simple interface for predicates over generic types.
 */
public interface Predicate<T>
{
    /**
       Execute the predicate on a value.

       @param v the value to be tested.
       @return the result of the test.
     */
    public boolean test(T v);
}
