package stixar.graph.attr;

import stixar.graph.Node;

/**
   Read-only generic node attribute set.
 */
public interface NodeSource<T> extends AttrSource<T>, NodeData
{
    /**
       Retrieve the attribute for the data.

       @param n the node whose attribute is to be queried.
       @return the attribute for <tt>n</tt> from this attribute set.
     */
    public T get(Node n);
}