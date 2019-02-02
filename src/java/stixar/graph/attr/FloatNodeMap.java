package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native floats.
 */
public class FloatNodeMap extends FloatMap
    implements NativeNodeMap
{
    public FloatNodeMap(float[] data)
    {
        super(data);
    }

    public float get(Node n)
    {
        return data[n.nodeId()];
    }

    public float set(Node n, float v)
    {
        return data[n.nodeId()] = v;
    }
}