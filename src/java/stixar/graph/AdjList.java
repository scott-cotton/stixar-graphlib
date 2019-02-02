package  stixar.graph;

import stixar.graph.order.NodeOrder;

import java.util.Iterator;

/**
   Simple adjacency list implementation of the {@link Digraph} interface.

   This class is primarily intended for use with programmer-supplied
   {@link Node} and {@link Edge} classes, giving an easy way to use
   many of the supplied algorithms with arbitrary application-specific 
   graphical data structures.
 */
class AdjList extends FilterGraph
    implements Graph
{
    /*
      We store the nodes in a simple array, and maintain 
      the invariant that nodes[i].nodeId() == i.
     */
    protected Node[] nodes;
    protected int numEdges;

    /**
       Construct an adjacency list from a set of nodes in
       the array <tt>nodes</tt>.
       <p>
       It is assumed that the nodes have {@link Node#nodeId ids} which are
       equal to the index in the array nodes.  Neither the array nor
       the contained nodes are copied to produce the graph.
       </p>
       
       @param nodes the nodes to be vertices in the resulting graph.
     */
    public AdjList(Node[] nodes)
    {
        super(nodes.length, 0);
        this.nodes = nodes;
        numEdges = 0;
        for (Edge e : edges()) numEdges++;
    }

    /**
       Construct an adjacency list graph from a set of nodes,
       with an explicit declaration of the number of edges.
       <p>
       If <tt>numEdges</tt> is incorrecct, subsequent calls
       to {@link #edgeSize} will give misinformation, leading
       to various problems including edge attribute arrays 
       of incorrect size.
       </p>
       
       @param nodes the nodes in the graph.
       @param numEdges the total number of edges in the graph.
     */
    public AdjList(Node[] nodes, int numEdges)
    {
        super(nodes.length, numEdges);
        this.nodes = nodes;
        this.numEdges = numEdges;
    }

    /**
       Make the nodes of the graph iterable.
     */
    public Iterable<Node> nodes() { 
        return new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new AdjListNodeIterator(nodes);
            }
        };
    }


    /**
       Make the nodes of the graph iterable.
     */
    public Iterable<Node> nodes(final NodeOrder order) { 
        return new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new OrderAdjListNodeIterator(nodes, order);
            }
        };
    }

    /**
       Make the edges of the graph iterable.
     */
    public Iterable<Edge> edges() {
        return new Iterable<Edge>() {
            public Iterator<Edge> iterator() {
                return new AdjListEdgeIterator(nodes);
            }
        };
    }

    /**
       Return the node of the graph with id <tt>id</tt>.
     */
    public Node node(int id) { return nodes[id]; }

    /**
       Return the number of nodes.
     */
    public int nodeSize() { return nodes.length; }

    public int nodeAttrSize() { return nodes.length; }

    /**
       Return the number of edges.
     */
    public int edgeSize() { return numEdges; }

    public int edgeAttrSize() { return numEdges; }


    /**
       Produces a string for the graph in a simple adjacency list 
       representation.  Each node <tt>n</tt> is listed in order (by {@link Node#nodeId}), 
       followed by  a colon ':' and a space separated list of the nodes
       to which <tt>n</tt> is linked by an edge.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Node n : nodes) {
            sb.append(n.nodeId() + ":");
            for (Edge e = n.out(); e != null; e = e.next()) {
                sb.append(" " + e.target().nodeId());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}



final class AdjListNodeIterator implements Iterator<Node> {
    protected Node[] nodes;
    protected int idx;

    public AdjListNodeIterator(Node[] nodes)
    {
        this.nodes = nodes;
        this.idx = 0;
    }

    public final boolean hasNext()
    {
        return idx < nodes.length;
    }

    public final Node next()
    {
        return nodes[idx++];
    }

    public final void remove()
    {
        throw new UnsupportedOperationException();
    }
}


final class OrderAdjListNodeIterator implements Iterator<Node> {
    protected Node[] nodes;
    protected int[] permutation;
    protected boolean reverse;
    protected int idx;

    public OrderAdjListNodeIterator(Node[] nodes, final NodeOrder order)
    {
        this.nodes = nodes;
        this.permutation = order.permutation();
        this.reverse = order.reversed();
        this.idx = reverse ? nodes.length - 1 : 0;
    }

    public final boolean hasNext()
    {
        return idx < nodes.length && idx >= 0;
    }

    public final Node next()
    {
        if (reverse) 
            return nodes[permutation[idx--]];
        else
            return nodes[permutation[idx++]];
    }

    public final void remove()
    {
        throw new UnsupportedOperationException();
    }
}



final class AdjListEdgeIterator implements Iterator<Edge> {
    protected Node[] nodes;
    protected int idx;
    protected Edge edge;

    public AdjListEdgeIterator(Node[] nodes)
    {
        this.nodes = nodes;
        this.idx = 0;
        this.edge = null;
        while(edge == null && idx < nodes.length) {
            edge = nodes[idx++].out();
        }
    }

    public final boolean hasNext()
    {
        return edge != null;
    }

    public final Edge next()
    {
        Edge result = edge;
        edge = edge.next();
        while(edge == null && idx < nodes.length) {
            edge = nodes[idx++].out();
        }
        return result;
    }

    public final void remove()
    {
        throw new UnsupportedOperationException();
    }
}


