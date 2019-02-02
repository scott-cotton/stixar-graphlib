package stixar.graph;

import stixar.graph.attr.Attributable;

/**
   Simple linked-list like interface for edges (readonly).

   This interface defines a minimal edge interface.  Programmers may implement
   this and the corresponding {@link Node} interfaces with a minimum of effort
   and then benefit from all the supplied read-only graph algorithms.
 */
public interface Edge extends Attributable
{
    /**
       Return the node from which this edge emanates.
       <b>Directed/Undirected Caveats:</b>
       <ul>
       <li>If this edge is a directed edge, this method will always return the same value.</li>
       <li>If this edge is an undirected edge, and is accessed through a (possibly empty) 
       sequence of calls to {@link #next} following a call to {@link Node#out} for a 
       node <tt>u</tt>, then this method will return <tt>u</tt>.
       </li>
       </ul>
     */
    public Node source();

    /**
       Return the node to which this edge points.
       <b>Directed/Undirected Caveats:</b>
       <ul>
       <li>If this edge is a directed edge, this method will always return the same value.</li>
       <li>If this edge is an undirected edge, and is accessed through a (possibly empty) 
       sequence of calls to {@link #next} following a call to {@link Node#out} for a 
       node <tt>u</tt>, then this method will return the vertex opposing <tt>u</tt>.
       </li>
       </ul>
     */
    public Node target();

    /**
       Return the next edge in the linked list of edges,
       or <tt>null</tt> if no such edge exists.
       <p>
       <b>Directed/Undirected Caveats:</b><br></br>
       The linked list whose existence is implied by this method should 
       respect the conventions detailed in the {@link Node#out} method.
       </p>
       @return the next edge in a list defined by the {@link Node#out} method.
     */
    public Edge next();


    /**
       Give a unique edge identifier.
       <p>
       <b>Directed/Undirected Caveats:</b><br></br>
       An edge must be equal to another edge iff their edgeIds are equal.
       This mechanism can be used to make pairs of objects assume the identity
       of a single edge which can be useful in making undirected edges.
    */
    public int edgeId();

}
