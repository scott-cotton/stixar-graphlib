/**
   Network flows and cuts.

   <p>
   <h3>Flows</h3>
   Network flows are defined for weighted digraphs <tt>(V,E,s,t,c)</tt>
   with distinguished  source (<tt>s</tt>) and sink (<tt>t</tt>) 
   nodes and where the weights (<tt>c</tt>) define non negative capacities for the edges.
   A flow <tt>f</tt> is a set of weights assigned to the edges which satisfy
   the following properties:
   <ol>
   <li>Capacity constraints. <tt>0 &lt;= f(e) &lt;= c(e)</tt> for every edge <tt>e</tt></li>
   <li>Conservation. The sum of the incoming flow to a vertex <tt>u</tt> is equal
   to the sum of its outgoing flow, for all vertices except the source and sink.</li>
   </ol>
   This algorithm computes a <em>maximum</em> flow, or a flow in which the
   sum of the edge weights is maximal amongst all flows.
   </p>
   <p>
   <h3>Cuts</h3>
   A cut may be defined as a set of vertices <tt>S</tt>in an st graph such that
   the removal of the edges <tt>S x (V-S)</tt> separates <tt>s</tt> from <tt>t</tt>.
   As a maximum flow corresponds directly to a minimum cut, this algorithm
   computes min cuts as well as max flows.
   <p>
   <h3>Representation</h3>
   In general, flows are specified on asymmetric graphs, but most algorithms for
   the max flow problem, including this one, require symmetric graphs to run.  This
   implementation can translate a non symmetric graph to an appropriate symmetric 
   one in the case that it is passed a {@link stixar.graph.MutableDigraph}.  Otherwise, it requires
   the results of symmetrization be made available in the form of edge attributes
   which point each edge <tt>(u,v)</tt> to its reverse edge <tt>(v,u)</tt>.  If
   the graph is externally symmetrized, then all the edges which are added in 
   the symmetrization process should have zero capacity.
   </p>

 */
package stixar.graph.flow;
