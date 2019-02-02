package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native ints.
 */
public class IntNodeMap extends IntMap
    implements NativeNodeMap
{
    public IntNodeMap(int[] data)
    {
        super(data);
    }

    public int get(Node n)
    {
        return data[n.nodeId()];
    }

    public int set(Node n, int v)
    {
        return data[n.nodeId()] = v;
    }
}