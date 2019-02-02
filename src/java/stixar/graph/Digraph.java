package stixar.graph;

import stixar.graph.order.NodeOrder;

/**
   Simple marker interface for a directed graph.  The edges in a digraph
   should behave as directed edges, that is, for two edges <tt>e</tt> and <tt>e'</tt>:
   <ol>
   <li><tt>e.source() != e'.source()</tt> implies <tt>e.equals(e')</tt> is <tt>false</tt></li>
   <li>Similarly for the {@link Edge#target target}.</li>
   <li><tt>e.equals(e')</tt> exactly if <tt>e.edgeId() == e'.edgeId()</tt>.</li>
   </ol>
 */
public interface Digraph extends Graph
{}
