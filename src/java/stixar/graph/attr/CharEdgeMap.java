package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native chars.
 */
public class CharEdgeMap extends CharMap
    implements NativeEdgeMap
{
    public CharEdgeMap(char[] data)
    {
        super(data);
    }

    public char get(Edge e)
    {
        return data[e.edgeId()];
    }

    public char set(Edge e, char v)
    {
        return data[e.edgeId()] = v;
    }
}