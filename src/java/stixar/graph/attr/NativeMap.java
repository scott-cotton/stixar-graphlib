package stixar.graph.attr;

/**
   Interface for any attribute map implemented with native (non-generic)
   number types.
 */
public interface NativeMap
{
    /**
       The set of available native attribute types.
     */
    public enum Type { Int, Long, Double, Float, Char, Byte }

    /**
       Return the type of attribute this attribute map contains.
     */
    public Type type();
}