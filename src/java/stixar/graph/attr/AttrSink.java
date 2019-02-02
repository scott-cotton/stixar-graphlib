package stixar.graph.attr;

/**
   Interface for something which can get and set attributes.
 */
public interface AttrSink<T>
{
    /**
       Set the attribute of something with an identifier <tt>i</tt>.
       @param i the identifier of an attributable object.
       @return v
     */
    public T set(int i, T v);
}
