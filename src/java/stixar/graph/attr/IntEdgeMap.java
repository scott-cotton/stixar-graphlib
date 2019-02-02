package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native ints.
 */
public class IntEdgeMap extends IntMap
    implements NativeEdgeMap
{
    public IntEdgeMap(int[] data)
    {
        super(data);
    }

    public int get(Edge e)
    {
        return data[e.edgeId()];
    }

    public int set(Edge e, int v)
    {
        return data[e.edgeId()] = v;
    }
}