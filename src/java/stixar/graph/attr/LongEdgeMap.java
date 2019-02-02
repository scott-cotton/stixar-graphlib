package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native longs.
 */
public class LongEdgeMap extends LongMap
    implements NativeEdgeMap
{
    public LongEdgeMap(long[] data)
    {
        super(data);
    }

    public long get(Edge e)
    {
        return data[e.edgeId()];
    }

    public long set(Edge e, long v)
    {
        return data[e.edgeId()] = v;
    }
}