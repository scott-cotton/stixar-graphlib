package stixar.graph.attr;

import stixar.graph.Node;

public interface NodeMatrix<T> extends NodeData
{
    public T get(Node u, Node v);
    public T set(Node u, Node v, T val);
}
