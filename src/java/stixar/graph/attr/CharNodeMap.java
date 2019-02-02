package stixar.graph.attr;

import stixar.graph.Node;

/**
   Node map containing native chars.
 */
public class CharNodeMap extends CharMap
    implements NativeNodeMap
{
    public CharNodeMap(char[] data)
    {
        super(data);
    }

    public char get(Node n)
    {
        return data[n.nodeId()];
    }

    public char set(Node n, char v)
    {
        return data[n.nodeId()] = v;
    }
}