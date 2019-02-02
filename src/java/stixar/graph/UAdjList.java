package stixar.graph;


class UAdjList extends AdjList
    implements UGraph
{
    public UAdjList(Node[] nodes)
    {
        super(nodes);
    }

    public UAdjList(Node[] nodes, int edgeCount)
    {
        super(nodes, edgeCount);
    }

}