package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native doubles.
 */
public class DoubleEdgeMap extends DoubleMap
    implements NativeEdgeMap
{
    public DoubleEdgeMap(double[] data)
    {
        super(data);
    }

    public double get(Edge e)
    {
        return data[e.edgeId()];
    }

    public double set(Edge e, double v)
    {
        return data[e.edgeId()] = v;
    }
}