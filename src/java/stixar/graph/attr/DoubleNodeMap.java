package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native doubles.
 */
public class DoubleNodeMap extends DoubleMap
    implements NativeNodeMap
{
    public DoubleNodeMap(double[] data)
    {
        super(data);
    }

    public double get(Node n)
    {
        return data[n.nodeId()];
    }

    public double set(Node n, double v)
    {
        return data[n.nodeId()] = v;
    }
}