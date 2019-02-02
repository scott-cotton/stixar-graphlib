package stixar.graph.attr;

import stixar.graph.Edge;

/**
   Edge map containing native bytes.
 */
public class ByteEdgeMap extends ByteMap
    implements NativeEdgeMap
{
    public ByteEdgeMap(byte[] data)
    {
        super(data);
    }

    public byte get(Edge e)
    {
        return data[e.edgeId()];
    }

    public byte set(Edge e, byte v)
    {
        return data[e.edgeId()] = v;
    }
}