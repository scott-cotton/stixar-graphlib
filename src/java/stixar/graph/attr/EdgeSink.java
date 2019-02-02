package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Write-only generic edge attribute interface.
 */
public interface EdgeSink<T> extends AttrSink<T>, EdgeData
{
    /**
       Set an edges attribute.

       @param e the edge whose attribute is to be set.
       @param v the new attribute value.
     */
    public T set(Edge e, T v);
}