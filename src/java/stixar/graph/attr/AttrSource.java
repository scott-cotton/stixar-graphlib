package stixar.graph.attr;

/**
   Interface for something which can get attributes.
 */
public interface AttrSource<T>
{
    public T get(int i);
}
