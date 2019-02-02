package stixar.graph.paths;

import stixar.graph.Graph;
import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.attr.EdgeSource;
import stixar.graph.attr.NativeEdgeMap;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.NativeNodeMap;

import stixar.graph.check.AcyclicChecker;

import stixar.util.NumAdaptor;

/**
   Single source shortest path computations as static methods.

   @see Dijkstra 
   @see AcyclicSP 
   @see BFM
 */
public class SSSP
{

    // get rid of javadocs, it's a purely static class.
    private SSSP() {}

    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
     */
    public static <T> void dijkstra(Graph g, 
                                    Node source, 
                                    NodeMap<T> distMap,
                                    EdgeSource<T> edgeWeights,
                                    NumAdaptor<T> adaptor)
    {
        Dijkstra<T> d = new Dijkstra<T>(g, 
                                        source, 
                                        null, 
                                        null, 
                                        distMap, 
                                        edgeWeights, 
                                        adaptor, 
                                        null);
        d.run();
    }
    
    
    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  If a shortest path
       from the source to a vertex <tt>v</tt> ends with an edge
       <tt>(u,v)</tt>, then the edge <tt>(u,v)</tt> is placed in the 
       <tt>predMap</tt> for the vertex <tt>v</tt>.
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param predMap (out) a node map in which to store the shortest paths tree.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
    */
    public static <T> void dijkstra(Graph g, 
                                    Node source, 
                                    NodeMap<T> distMap,
                                    EdgeSource<T> edgeWeights,
                                    NodeMap<Edge> predMap,
                                    NumAdaptor<T> adaptor)
    {
        Dijkstra<T> d = new Dijkstra<T>(g, 
                                        source, 
                                        null, 
                                        predMap, 
                                        distMap, 
                                        edgeWeights, 
                                        adaptor, 
                                        null);
        d.run();
    }
    
    
    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  This process continues
       until the distance to <tt>target</tt> is found, at which time
       the algorithm halts.
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
    */
    public static <T> void dijkstra(Graph g, 
                                    Node source, 
                                    Node target,
                                    NodeMap<T> distMap,
                                    EdgeSource<T> edgeWeights,
                                    NumAdaptor<T> adaptor)
    {
        Dijkstra<T> d = new Dijkstra<T>(g,
                                        source, 
                                        target, 
                                        null, 
                                        distMap, 
                                        edgeWeights, 
                                        adaptor, 
                                        null);
        d.run();
    }
    
    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  If a shortest path
       from the source to a vertex <tt>v</tt> ends with an edge
       <tt>(u,v)</tt>, then the edge <tt>(u,v)</tt> is placed in the 
       <tt>predMap</tt> for the vertex <tt>v</tt>.  Once the distance to 
       <tt>target</tt> is found, the algorithm halts.
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param predMap (out) a node map in which to store the shortest paths tree.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
    */
    public static <T> void dijkstra(Graph g, 
                                    Node source, 
                                    Node target,
                                    NodeMap<T> distMap,
                                    NodeMap<Edge> predMap,
                                    EdgeSource<T> edgeWeights,
                                    NumAdaptor<T> adaptor)
    {
        Dijkstra<T> d = new Dijkstra<T>(g, 
                                        source, 
                                        target, 
                                        predMap,
                                        distMap, 
                                        edgeWeights, 
                                        adaptor, 
                                        null);
        d.run();
    }
    
    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights using
       native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  The implemented native
       types are: <tt>int,long,float</tt>, and <tt>double</tt>.
       
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void dijkstra(Graph g, 
                                Node source, 
                                NativeNodeMap distMap, 
                                NativeEdgeMap edgeWeights)
    {
        DijkstraNative d = new DijkstraNative(g, 
                                              source, 
                                              null, 
                                              null, 
                                              distMap, 
                                              edgeWeights, 
                                              null);
        d.run();
    }

    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights using
       native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt> is
       constructed.  The distance from the source to every vertex in the tree
       is placed in <tt>distMap</tt>.  The implemented native types are:
       <tt>int,long,float</tt>, and <tt>double</tt>.  If a shortest path from
       the source to a vertex <tt>v</tt> ends with an edge <tt>(u,v)</tt>, then
       the edge <tt>(u,v)</tt> is placed in the <tt>predMap</tt> for the vertex
       <tt>v</tt>
       </p>

       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void dijkstra(Graph g, 
                                Node source, 
                                NativeNodeMap distMap, 
                                NativeEdgeMap edgeWeights,
                                NodeMap<Edge> parentMap)
    {
        DijkstraNative d = new DijkstraNative(g, 
                                              source, 
                                              null, 
                                              parentMap, 
                                              distMap, 
                                              edgeWeights, 
                                              null);
        d.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights using
       native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  The implemented native
       types are: <tt>int,long,float</tt>, and <tt>double</tt>.  Once
       a shortest path to <tt>target</tt> is found, the algorithm stops.
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void dijkstra(Graph g, 
                                Node source, 
                                Node target,
                                NativeNodeMap distMap, 
                                NativeEdgeMap edgeWeights)
    {
        DijkstraNative d = new DijkstraNative(g, 
                                              source, 
                                              target, 
                                              null, 
                                              distMap, 
                                              edgeWeights, 
                                              null);
        d.run();
    }

    /**
       Compute the single source shortests paths problem for 
       an arbitrary graph with non negative edge weights using
       native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt> is
       constructed.  The distance from the source to every vertex in the tree
       is placed in <tt>distMap</tt>.  The implemented native types are:
       <tt>int,long,float</tt>, and <tt>double</tt>.  If a shortest path from
       the source to a vertex <tt>v</tt> ends with an edge <tt>(u,v)</tt>, then
       the edge <tt>(u,v)</tt> is placed in the <tt>predMap</tt> for the vertex
       <tt>v</tt>. Once a shortest path to <tt>target</tt> is found, the algorithm 
       stops. 
       </p>

       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void dijkstra(Graph g, 
                                Node source, 
                                Node target,
                                NativeNodeMap distMap, 
                                NativeEdgeMap edgeWeights,
                                NodeMap<Edge> parentMap)
    {
        DijkstraNative d = new DijkstraNative(g, 
                                              source, 
                                              target, 
                                              parentMap, 
                                              distMap, 
                                              edgeWeights, 
                                              null);
        d.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an acyclic digraph.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  If a shortest path
       from the source to a vertex <tt>v</tt> ends with an edge
       <tt>(u,v)</tt>, then the edge <tt>(u,v)</tt> is placed in the 
       <tt>predMap</tt> for the vertex <tt>v</tt>.  Once the distance to 
       <tt>target</tt> is found, the algorithm halts.  If the source vertex is
       <tt>null</tt>, the shortest path forest from all the depth first
       search roots is computed.
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param predMap (out) a node map in which to store the shortest paths tree.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
     */
    public static <T> void acyclic(Digraph g, 
                                   Node source, 
                                   Node target,
                                   NodeMap<T> distMap,
                                   NodeMap<Edge> predMap,
                                   EdgeSource<T> edgeWeights,
                                   NumAdaptor<T> adaptor)
    {
        AcyclicSP<T> a = new AcyclicSP<T>(g,
                                          source, 
                                          target, 
                                          distMap, 
                                          edgeWeights,
                                          predMap,
                                          adaptor);
        a.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an acyclic digraph.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  If a shortest path
       from the source to a vertex <tt>v</tt> ends with an edge
       <tt>(u,v)</tt>, then the edge <tt>(u,v)</tt> is placed in the 
       <tt>predMap</tt> for the vertex <tt>v</tt>.    If the source vertex is
       <tt>null</tt>, the shortest path forest from all the depth first
       search roots is computed.
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param predMap (out) a node map in which to store the shortest paths tree.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
     */
    public static <T> void acyclic(Digraph g, 
                                   Node source, 
                                   NodeMap<T> distMap,
                                   NodeMap<Edge> predMap,
                                   EdgeSource<T> edgeWeights,
                                   NumAdaptor<T> adaptor)
    {
        AcyclicSP<T> a = new AcyclicSP<T>(g,
                                          source, 
                                          null,
                                          distMap, 
                                          edgeWeights,
                                          predMap,
                                          adaptor);
        a.run();
    }

    /**
       Compute the single source shortests paths problem for 
       an acyclic digraph.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  Once the distance to 
       <tt>target</tt> is found, the algorithm halts.  If the source vertex is
       <tt>null</tt>, the shortest path forest from all the depth first
       search roots is computed.
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
     */
    public static <T> void acyclic(Digraph g, 
                                   Node source, 
                                   Node target,
                                   NodeMap<T> distMap,
                                   EdgeSource<T> edgeWeights,
                                   NumAdaptor<T> adaptor)
    {
        AcyclicSP<T> a = new AcyclicSP<T>(g,
                                          source, 
                                          target, 
                                          distMap, 
                                          edgeWeights,
                                          null,
                                          adaptor);
        a.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an acyclic digraph.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  If the source vertex is
       <tt>null</tt>, the shortest path forest from all the depth first
       search roots is computed.
       </p>
       @param g (in) the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       @param adaptor (in) an adaptor defining addition, comparison, zero, and infinity
       for the type <tt>T</tt>.
     */
    public static <T> void acyclic(Digraph g, 
                                   Node source, 
                                   NodeMap<T> distMap,
                                   EdgeSource<T> edgeWeights,
                                   NumAdaptor<T> adaptor)
    {
        AcyclicSP<T> a = new AcyclicSP<T>(g,
                                          source, 
                                          null,
                                          distMap, 
                                          edgeWeights,
                                          null,
                                          adaptor);
        a.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an acyclic graph using native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  The implemented native
       types are: <tt>int,long,float</tt>, and <tt>double</tt>.
       
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void acyclic(Digraph g, 
                               Node source, 
                               NativeNodeMap distMap, 
                               NativeEdgeMap edgeWeights)
    {
        AcyclicSPNative d = new AcyclicSPNative(g, 
                                                source, 
                                                null, 
                                                distMap,
                                                edgeWeights,
                                                null);
        d.run();
    }
    
    /**
       Compute the single source shortests paths problem for 
       an acyclic graph using native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt> is
       constructed.  The distance from the source to every vertex in the tree
       is placed in <tt>distMap</tt>.  The implemented native types are:
       <tt>int,long,float</tt>, and <tt>double</tt>.  If a shortest path from
       the source to a vertex <tt>v</tt> ends with an edge <tt>(u,v)</tt>, then
       the edge <tt>(u,v)</tt> is placed in the <tt>predMap</tt> for the vertex
       <tt>v</tt>
       </p>

       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void acyclic(Digraph g, 
                               Node source, 
                               NativeNodeMap distMap, 
                               NativeEdgeMap edgeWeights,
                               NodeMap<Edge> parentMap)
    {
        AcyclicSPNative d = new AcyclicSPNative(g, 
                                                source, 
                                                null, 
                                                distMap, 
                                                edgeWeights,
                                                parentMap);
        d.run();
    }


    /**
       Compute the single source shortests paths problem for 
       an acyclic graph using native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt>
       is constructed.  The distance from the source to every vertex in
       the tree is placed in <tt>distMap</tt>.  The implemented native
       types are: <tt>int,long,float</tt>, and <tt>double</tt>.  Once
       a shortest path to <tt>target</tt> is found, the algorithm stops.
       </p>
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param target (in) the target vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
     */
    public static void acyclic(Digraph g, 
                               Node source, 
                               Node target,
                               NativeNodeMap distMap, 
                               NativeEdgeMap edgeWeights)
    {
        AcyclicSPNative d = new AcyclicSPNative(g, 
                                                source, 
                                                target, 
                                                distMap, 
                                                edgeWeights, 
                                                null);
        d.run();
    }

    /**
       Compute the single source shortests paths problem for 
       an acyclic graph using native maps.
       <p>
       A shortest paths tree for every reachable vertex from <tt>source</tt> is
       constructed.  The distance from the source to every vertex in the tree
       is placed in <tt>distMap</tt>.  The implemented native types are:
       <tt>int,long,float</tt>, and <tt>double</tt>.  If a shortest path from
       the source to a vertex <tt>v</tt> ends with an edge <tt>(u,v)</tt>, then
       the edge <tt>(u,v)</tt> is placed in the <tt>predMap</tt> for the vertex
       <tt>v</tt>. Once a shortest path to <tt>target</tt> is found, the algorithm 
       stops. 
       </p>
       
       @param g (in)the graph with which to find shortest paths.
       @param source (in) the source vertex.
       @param distMap (out) a node attribute map giving distances to each vertex.
       @param edgeWeights (in) an edge attribute map giving each edge a weight.
       for the type <tt>T</tt>.
       @throws IllegalArgumentException if the native types of the <tt>distMap</tt>
       and the <tt>edgeWeights</tt> are not equal or if the native types
       are not supported.
    */
    public static void acyclic(Digraph g, 
                               Node source, 
                               Node target,
                               NativeNodeMap distMap, 
                               NativeEdgeMap edgeWeights,
                               NodeMap<Edge> parentMap)
    {
        AcyclicSPNative d = new AcyclicSPNative(g, 
                                                source, 
                                                target, 
                                                distMap, 
                                                edgeWeights, 
                                                parentMap);
        d.run();
    }


    /**
       Compute the single source shortest path problem for a digraph with
       arbitrary (positive or negative) edge weights.  
       <p>
       A shortest path
       tree is computed rooted at <tt>source</tt>.  The distance to each
       vertex is placed in <tt>distMap</tt>.  If a vertex is not reachable,
       its distance <tt>adaptor.inf()</tt>.  If the shortest path tree includes
       the edge <tt>(u,v)</tt>, then <tt>parentMap</tt> associates with <tt>v</tt>
       the edge <tt>(u,v)</tt>.
       </p>
       @param g the digraph in which to compute shortest paths.
       @param distMap the node attribute map in which to put the distances.
       @param weights an edge attribute source describing edge sources.
       @param parentMap a node attribute map in which the shortest path
       tree is built by associating with each node its parent edge.
       @param adaptor the shortest paths adaptor for the type <tt>T</tt>.
       @return <tt>null</tt> if the shortests paths are well defined, that is 
       if there is no negative cyclic in <tt>g</tt> reachable from <tt>source</tt>.
       Otherwise, return a {@link Path} which is a negative cycle.
     */
    public static <T> Path arbw(Digraph g,
                                Node source,
                                NodeMap<T> distMap,
                                EdgeSource<T> weights,
                                NodeMap<Edge> parentMap,
                                NumAdaptor<T> adaptor)
    {
        BFM<T> a = new BFM<T>(g,
                              source, 
                              weights,
                              distMap,
                              parentMap,
                              adaptor);
        a.run();
        if (a.negCycleEdge() != null)
            return a.negCycle();
        return null;
    }

    /**
       Compute the single source shortest path problem for a digraph with
       arbitrary (positive or negative) edge weights.  
       <p>
       A shortest path
       tree is computed rooted at <tt>source</tt>.  The distance to each
       vertex is placed in <tt>distMap</tt>.  If a vertex is not reachable,
       its distance <tt>adaptor.inf()</tt>. 
       </p>
       @param g the digraph in which to compute shortest paths.
       @param distMap the node attribute map in which to put the distances.
       @param weights an edge attribute source describing edge sources.
       @param adaptor the shortest paths adaptor for the type <tt>T</tt>.
       @return <tt>null</tt> if the shortests paths are well defined, that is 
       if there is no negative cyclic in <tt>g</tt> reachable from <tt>source</tt>.
       Otherwise, return a {@link Path} which is a negative cycle.
     */
    public static <T> Path arbw(Digraph g,
                                Node source,
                                NodeMap<T> distMap,
                                EdgeSource<T> weights,
                                NumAdaptor<T> adaptor)
    {
        BFM<T> a = new BFM<T>(g,
                              source, 
                              weights,
                              distMap,
                              null,
                              adaptor);
        a.run();
        if (a.negCycleEdge() != null)
            return a.negCycle();
        return null;
    }

    /**
       Compute the single source shortest path problem for a digraph with
       arbitrary (positive or negative) edge weights.  
       <p>
       A shortest path
       tree is computed rooted at <tt>source</tt>.  The distance to each
       vertex is placed in <tt>distMap</tt>.  If a vertex is not reachable,
       its distance corresponds to the maximum value of the native type
       used, such as <tt>Integer.MAX_VALUE</tt>..  If the shortest path tree includes
       the edge <tt>(u,v)</tt>, then <tt>parentMap</tt> associates with <tt>v</tt>
       the edge <tt>(u,v)</tt>.  
       <tt>int,long,float,</tt> and <tt>double</tt> 
       native types are supported
       </p>
       @param g the digraph in which to compute shortest paths.
       @param distMap the node attribute map in which to put the distances.
       @param weights an edge attribute source describing edge sources.
       @param parentMap a node attribute map in which the shortest path
       tree is built by associating with each node its parent edge.
       @return <tt>null</tt> if the shortests paths are well defined, that is 
       if there is no negative cyclic in <tt>g</tt> reachable from <tt>source</tt>.
       Otherwise, return a {@link Path} which is a negative cycle.
       @throws IllegalArgumentException if the native type is not supported.
       @throws ClassCastException if the native type of <tt>distMap</tt> is not
       the same as the native type of <tt>weights</tt>.
     */
    public static Path arbw(Digraph g,
                            Node source,
                            NativeNodeMap distMap,
                            NativeEdgeMap weights,
                            NodeMap<Edge> parentMap)
    {
        BFMNative a = new BFMNative(g,
                                    source, 
                                    weights,
                                    distMap,
                                    parentMap);
        a.run();
        if (a.negCycleEdge() != null)
            return a.negCycle();
        return null;
    }

    /**
       Compute the single source shortest path problem for a digraph with
       arbitrary (positive or negative) edge weights.  
       <p>
       A shortest path
       tree is computed rooted at <tt>source</tt>.  The distance to each
       vertex is placed in <tt>distMap</tt>.  If a vertex is not reachable,
       its distance is the maximum value for the corresponding native type.
       <tt>int,long,float,</tt> and <tt>double</tt> 
       native types are supported
       </p>
       @param g the digraph in which to compute shortest paths.
       @param distMap the node attribute map in which to put the distances.
       @param weights an edge attribute source describing edge sources.
       @return <tt>null</tt> if the shortests paths are well defined, that is 
       if there is no negative cyclic in <tt>g</tt> reachable from <tt>source</tt>.
       Otherwise, return a {@link Path} which is a negative cycle.
       @throws IllegalArgumentException if the native type is not supported.
       @throws ClassCastException if the native type of <tt>distMap</tt> is not
       the same as the native type of <tt>weights</tt>.
     */
    public static Path arbw(Digraph g,
                            Node source,
                            NativeNodeMap distMap,
                            NativeEdgeMap weights)
    {
        BFMNative a = new BFMNative(g,
                                    source, 
                                    weights,
                                    distMap,
                                    null);
        a.run();
        if (a.negCycleEdge() != null)
            return a.negCycle();
        return null;
    }
}
