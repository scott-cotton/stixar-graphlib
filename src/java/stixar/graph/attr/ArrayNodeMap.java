package stixar.graph.attr;

import stixar.graph.Node;

/**
   Array backed generic attribute map.
 */
public final class ArrayNodeMap<T> extends ArrayMap<T>
    implements NodeMap<T>
{
    public ArrayNodeMap(T[] data)
    {
        super(data);
    }

    public T get(Node n)
    {
        return data[n.nodeId()];
    }

    public T set(Node n, T v)
    {
        return data[n.nodeId()] = v;
    }
}