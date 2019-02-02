package stixar.graph;

/**
   Marker for a graph with undirected edges.
   The edges for an undirected graph should behave with undirected semantics:
   <ol>
   <li>The {@link Node#out} method should return all adjacent edges.</li>
   <li>The edges returned from the {@link Node#out} method for a node
   <tt>n</tt> should return <tt>n</tt> in response to the {@link Edge#source} 
   method, and the opposing node in response to the {@link Edge#target} method.
   </li>
   <li>Even when two identical undirected edges <tt>e,e'</tt>return different values for {@link Edge#source}
   and {@link Edge#target} due to the calling context, both of the following remain true:
       <ol>
       <li><tt>e.equals(e')</tt> should hold.</li>
       <li><tt>e.edgeId() == e'.edgeId()</tt> should hold.
       <li>Attribute getters and setters should refer to the same attributes.</li>
       </ol>
   </li>
   </ol>
   </ol>
 */
public interface UGraph extends Graph
{
}
