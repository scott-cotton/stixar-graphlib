package stixar.graph.attr;

import stixar.graph.Edge;

/**
   An array backed implementation of an {@link EdgeMap}.
 */
public final class ArrayEdgeMap<T> extends ArrayMap<T>
    implements EdgeMap<T>
{
    public ArrayEdgeMap(T[] data)
    {
        super(data);
    }

    public T get(Edge e)
    {
        return data[e.edgeId()];
    }

    public T set(Edge e, T v)
    {
        return data[e.edgeId()] = v;
    }
}