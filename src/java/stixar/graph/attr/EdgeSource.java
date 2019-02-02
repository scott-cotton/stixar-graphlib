package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Read only generic edge attribute set.
 */
public interface EdgeSource<T> extends AttrSource<T>, EdgeData
{
    /**
       Edge attribute data getter.

       @param e the edge whose attribute data is to be retrieved.
       @return the attribute data associated with <tt>e</tt>
     */
    public T get(Edge e);
}