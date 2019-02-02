package stixar.graph;

import stixar.graph.attr.Attributable;
/**
   A very basic node interface.

   This interface is the minimal node interface.  Programmers may 
   implement this and the corresponding {@link Edge} interfaces 
   with a minimum of effort and then benefit from all the supplied 
   read-only graph algorithms.
 */
public interface Node extends Attributable
{
    /** 
        Node identifier.

        @see Digraph#nodeSize
     */
    public int nodeId();

    /**
       Produce an outgoing edge in the list of outgoing edges,
       if there is one. 
       <p>
       <b>Outgoing</b> for a node <tt>u</tt> means all edges of the 
       form <tt>(u,v)</tt> for directed graphs, and means all
       adjacent edges for undirected graphs.
       </p>

       @return the first in the list of outgoing edges or <tt>null</tt>
       if there is no such edge.
     */
    public Edge out();

}
