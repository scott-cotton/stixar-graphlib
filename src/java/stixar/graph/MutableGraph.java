package stixar.graph;

import java.util.List;
import java.util.Comparator;

/**
   Mutable Graph.
 */
public interface MutableGraph extends Graph
{

    /**
       Remove the node <tt>node</tt> from the graph together
       with any edges which have an endpoint in <tt>node</tt>.
       <p>
       <ul>
       <li>Subsequent calls to {@link Node#out} for the node will
       return <tt>null</tt>.</li>
       <li>Subsequent use of attribute data for the node will fail with
       an IndexOutOfBoundsException</li>
       </p>

       @param node the node to remove from the graph.
     */
    public void remove(Node node);


    /**
       Generate <tt>n</tt> new nodes for this graph.
       @param n the number of nodes to generate
       <p>
       Each new node will work seemlessly with managed attributes.
       Ad Hoc attributes in the form of native arrays should not
       be used after nodes are generated, as they <em>may</em> return spurious
       data or give an IndexOutOfBoundsException.
       </p>

       @return a list of the resulting nodes in order of increasing
       value for {@link Node#nodeId}.
     */
    public List<Node> genNodes(int n);

    /**
       Generate a single new node for this graph.

       @return the new node.
     */
    public Node genNode();

    /**
       Generate a new edge for this graph, such that the
       edge's {@link Edge#source} method returns <tt>u</tt>
       and the edge's {@link Edge#target} method returns <tt>v</tt>.

       @param u one endpoint of the edge, the starting endpoint if
       the edge is directed.

       @param v another endpoint of the edge, the ending endpoint if
       the edge is directed.
     */
    public Edge genEdge(Node u, Node v);


    /**
       Remove the edge from the graph. 
       <p>
       Once the method is complete, the edge's {@link Edge#source}
       and {@link Edge#target} methods will continue to point to the
       source and target node objects, but the {@link Edge#edgeId}
       method will return <tt>-edgeId() - 1</tt> and attempts to get or
       set attributes will fail with an <tt>IndexOutOfBoundsException</tt>.
       </p>
       @param edge the edge to be removed from the graph.
     */
    public void remove(Edge edge);

    /**
       Re-add a removed edge.
     */
    public void relink(Edge e);


    /**
       Move an edge specifying the new endpoints.
       <p>
       At the end of this method call, the edge's {@link Edge#source}
       method will return <tt>u</tt>, the edge's {@link Edge#target}
       method will return <tt>v</tt>, and the edge will be accessible
       via the appropriate {@link Node#out adjacency iteration methods}.
       </p>
       <p>
       This method does not necessarily make an edge directed, an
       undirected edge will retain the {@link UGraph undirected semantics}.
       </p>
       @param e the edge to be moved.
       @param u the new source endpoint
       @param v the new target endpoint.
     */
    public void moveEdge(Edge e, Node u, Node v);

    /**
       Sort the edges in the graph.
       <p>
       The edges are sorted so that the outgoing
       (or adjacent for undirected graphs) edges
       of a node are guaranteed to fall in a particular order.
       </p>
       @param cmp a comparator for sorting the edges.
     */
    public void sortEdges(Comparator<Edge> cmp);
    
    /**
       Attempt to ensure the underlying representation
       has room for <tt>n</tt> nodes and <tt>m</tt> edges.
       If the implementation is able to, it is to preallocate
       the storage.  Any managed attribute maps are to have the 
       storage allocated as well.

       @param n the number of nodes for which to create storage.
       @param m the number of edges for which to create storage.
     */
    public void ensureCapacity(int n, int m);

    /**
       Ensure that the underlying representation uses
       minimal space.
       <p>
       If the graph is not already trimmed, this operation 
       will invalidate ad-hoc attribute maps.
       </p>
       @see stixar.graph.attr.GraphAttrCollection
     */
    public void trimToSize();

    /**
       Clear all nodes and edges from the graph.
     */
    public void clear();

}
