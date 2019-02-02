package stixar.graph.order;

import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Graph;
import stixar.graph.search.DFS;
import stixar.graph.search.BFS;

import java.util.Comparator;
/**
   Representation for an ordering of the nodes in a digraph.

   @see Graph#nodes(NodeOrder order)
 */
public class NodeOrder 
    implements Comparator<Node>
{
    protected Graph graph;
    protected int[] permutation;
    protected boolean reverse;

    /**
       Construct a new node ordering for a digraph.
       The array <tt>perm</tt> is copied.

       @param g the graph for which the ordering is to apply.
       @param perm a permutation of the nodes in the graph as 
       an attribute array.
     */
    public NodeOrder(Graph g, int[] perm)
    {
        graph = g;
        permutation = new int[perm.length];
        System.arraycopy(perm, 0, permutation, 0, perm.length);
        reverse = false;
    }

    /**
       Copy constructor.
     */
    public NodeOrder(NodeOrder ord)
    {
        this(ord.graph, ord.permutation);
        reverse = ord.reverse;
    }

    /**
       Return true if the order is reversed.
     */
    public boolean reversed()
    {
        return reverse;
    }
    
    /**
       Reverse the order.
       This is a constant time operation just setting a reversal bit.
     */
    public void reverse()
    {
        reverse = !reverse;
    }

    /**
       Return a permutation.
       @return an array of ints <tt>p</tt> such that <tt>graph.node(p[i])</tt>
       is the <tt>i</tt>th element in the order.
     */
    public int[] permutation()
    {
        return permutation;
    }

    /**
       Return the graph to which this NodeOrder belongs.
     */
    public Graph graph()
    {
        return graph;
    }

    /**
       Compare two nodes.
     */
    public int compare(Node u, Node v)
    {
        int pu = u.getInt(permutation);
        int pv = v.getInt(permutation);
        int cmp = pu < pv ? -1 : (pu == pv) ? 0 : 1;
        if (reverse) 
            return -cmp;
        return cmp;
    }

    /**
       Produce an edge comparator from a node comparator.
       <p>
       Produce an edge comparator that is based on the
       lexicographic order of the pair of nodes associated
       with an edge.
       </p>
       Note that this can create a comparator that is not
       consistent with equals in the case of undirected
       edges.
       @return an edge comparator.
     */
    public static Comparator<Edge> getEdgeComparator(final Comparator<Node> cmp)
    {
        return new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2)
            {
                int c = cmp.compare(e1.source(), e2.source());
                if (c == 0)
                    return cmp.compare(e1.target(), e2.target());
                else
                    return c;
            }
        };
    }
}
