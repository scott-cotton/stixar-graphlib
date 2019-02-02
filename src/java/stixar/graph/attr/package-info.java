/**
   Attribute information for graphs.
   <p>
   Graphs are often decorated with many different kinds of attributes
   with differing usage requirements.  This package implements and specifies 
   a number of attribute mechanisms which are used throughout the 
   <a href="../package-summary.html">graph package</a> and are intended
   to be used in arbitrary contexts as well.
   </p>
   <p>
   In terms of implementation, one of the first distinctions to be made for attributes 
   of graph's vertices or edges is whether or not the underlying set of vertices 
   or edges may change.  From the users perspective, it is highly undesirable that
   attribute sets become invalid if they so much as add or remove an edge from a graph. 
   To address this need, this package implements <em>managed attributes</em>, which act 
   exactly like any of the other attribute mechanisms provided here with the exception
   that managed attributes are guaranteed to retain their integrity even after
   a graph changes topologically.  Managed attributes are in fact implemented by 
   the same classes as unmanaged attributes, but managed attributes are generated
   by an {@link stixar.graph.attr.AttrManager}.
   </p>
   <p>
   Unmanaged attributes should be extremely easy to use, and the attribute
   mechanism presented here allows the use of arbitrary native arrays <em>directly
   as attribute maps</em>.  For example, one can store a set of <tt>int</tt> node attributes
   in a plain <tt>int[]</tt> object, without in the least worrying about what classes
   to use in this attribute mechanism.  The nodes and edges can then get and set 
   the attributes directly from the array as in the following example.<br>
   <pre>
   int[] m = new int[graph.nodeAttrSize()];
   Random rnd = new Random();
   for (Node n : graph.nodes()) {
       int nodeAttribute = n.getInt(m);
       n.setInt(m, nodeAttribute + rnd.nextInt(rnd.nextInt(100)));
   }
   </pre>
   In addition, one can create ad hoc generic and native attribute maps
   easily.  An example follows.
   <pre>
   Random rnd = new Random();
   Double default = 0d;
   EdgeMap&lt;Double&gt; weights = graph.createEdgeMap(default);
   for (Edge e : graph.edges()) {
       e.set(weights, rnd.nextDouble());
   }
   </pre>
   </br>
   </p>
 */
package stixar.graph.attr;

