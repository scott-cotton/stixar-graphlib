package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native longs.
 */
public class LongNodeMap extends LongMap
    implements NativeNodeMap
{
    public LongNodeMap(long[] data)
    {
        super(data);
    }

    public long get(Node n)
    {
        return data[n.nodeId()];
    }

    public long set(Node n, long v)
    {
        return data[n.nodeId()] = v;
    }
}