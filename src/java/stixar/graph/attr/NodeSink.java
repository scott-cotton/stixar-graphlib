package stixar.graph.attr;

import stixar.graph.Node;

/**
   A write-only generic node attribute set.
 */
public interface NodeSink<T> extends AttrSink<T>, NodeData
{
    /**
       Node attribute setter.
    
       @param n the node whose attribute is to be set.
       @param v the new value for the attribute.
       @return v
     */
    public T set(Node n, T v);
}