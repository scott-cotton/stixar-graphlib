package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native floats.
 */
public class FloatEdgeMap extends FloatMap
    implements NativeEdgeMap
{
    public FloatEdgeMap(float[] data)
    {
        super(data);
    }

    public float get(Edge e)
    {
        return data[e.edgeId()];
    }

    public float set(Edge e, float v)
    {
        return data[e.edgeId()] = v;
    }
}