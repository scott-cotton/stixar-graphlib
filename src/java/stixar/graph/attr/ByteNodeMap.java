package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native bytes.
 */
public class ByteNodeMap extends ByteMap
    implements NativeNodeMap
{
    public ByteNodeMap(byte[] data)
    {
        super(data);
    }

    public byte get(Node n)
    {
        return data[n.nodeId()];
    }

    public byte set(Node n, byte v)
    {
        return data[n.nodeId()] = v;
    }
}