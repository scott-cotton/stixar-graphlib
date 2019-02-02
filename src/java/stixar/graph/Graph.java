package stixar.graph;

import stixar.graph.attr.GraphAttrCollection;
import stixar.graph.order.NodeOrder;

/**
   Interface for any kind of graph.
   <p>
   Graphs may be one of directed or undirected and one of mutable or read-only.
   </p>
   <p>
   Whether or not a graph is directed changes the semantics of some of the node
   and edge functions.  These changes are documented with the {@link Node}, {@link Edge},
   {@link Digraph}, and {@link UGraph} interfaces.  Although it is possible to have
   a graph which contains both directed and undirected edges, this library only supplies
   implementations of graphs which are either fully directed or fully undirected, and
   hence exactly one of the {@link Digraph} and {@link UGraph} interfaces will be 
   implemented.
   </p>
   <p>
   Mutable graphs implement the MutableGraph interface, which extends this interface
   with operations for adding, removing, and "moving" nodes and edges.
   </p>
 */
public interface Graph extends GraphAttrCollection
{
    /**
       Returns the number of nodes in the digraph.  If the graph
       is <em>not</em> a {@link MutableGraph}, the number of nodes
       indicates at the same time the range of the node identifiers.
       If <tt>nodeSize()</tt> returns <tt>s</tt>, then
       <ol>
       <li>There are <tt>s</tt> nodes in the graph</li>
       <li>Each node has a unique id in the range <tt>[0,s)</tt></li>
       </ol>
       @see Node#nodeId
     */
    public int nodeSize();

    /**
       Return the size of an attribute array for the graph.
       <p>
       The returned size is only guaranteed to be valid for as 
       long as the graph does not make use of the methods 
       specific to {@link MutableGraph}.
       </p>
       @return the minimal required size for an ad-hoc node attribute array.
     */
    public int nodeAttrSize();

    /**
       Returns the number of edges in the digraph, indicating
       at the same time the range of the edge identifiers.
       If <tt>edgeSize()</tt> return <tt>s</tt>, then
       <ol>
       <li> There are <tt>s</tt> edges in the graph</li>
       <li> Each edge has a unique id in the range <tt>[0,s)</tt></li>
       </ol>

       @see Edge#edgeId
     */
    public int edgeSize();

    /**
       Return the size of an attribute array for the graph.
       <p>
       The returned size is only guaranteed to be valid for as 
       long as the graph does not make use of the methods 
       specific to {@link MutableGraph}.
       </p>
       @return the minimal required size for an ad-hoc edge attribute array.
     */
    public int edgeAttrSize();

    /**
       Make the nodes iterable.
     */
    public Iterable<Node> nodes();

    /**
       Make the edges iterable.
     */
    public Iterable<Edge> edges();


    /**
       Make the nodes iterable in a predefined order
     */
    public Iterable<Node> nodes(NodeOrder order);

    /*
      Filtering.
     */

    /**
       Add a graph filter, restricting the nodes and/or edges in the graph.
     */
    public void addFilter(GraphFilter f);

    /**
       Remove the last added graph filter.

       @return the last added graph filter, or <tt>null</tt> if
       no such filter exists.
     */
    public GraphFilter removeFilter();

    /**
       Clear all the node filters.
     */
    public void clearFilters();

    /**
       Return the currently installed graph filter for this graph,
       which may be a {@link ListGraphFilter} implementing all
       the added GraphFilters.

       @return an GraphFilter for this graph, or <tt>null</tt> if
       no such filter exists.
     */
    public GraphFilter getFilter();

    /**
       Return the node object with id <tt>id</tt>
       <p>
       If this graph is mutable, the ids of nodes may change as a result of
       removing nodes or edges.
       Hence, this method should be used with care to guarantee the 
       expected Node is returned.
       </p>

       @throws IndexOutOfBoundsException if <tt>id</tt> is out of bounds.
     */
    public Node node(int id);
}
