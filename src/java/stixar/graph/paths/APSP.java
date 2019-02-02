package stixar.graph.paths;

import stixar.graph.Digraph;
import stixar.graph.BasicDigraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.attr.EdgeSource;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.NodeMatrix;
import stixar.graph.attr.NativeNodeMatrix;

import stixar.util.NumAdaptor;

/**
   All pairs shortests paths computations as static methods.
 */
public class APSP
{
    private APSP() {}

    /**
       Compute the all pairs shortests paths distances of a digraph
       with edge weights.  If the graph is sparse, then {@link #apspSparse}
       is used, otherwise {@link #apspDense} is used.

       @param dg the digraph whose shortests path distances are to be 
       computed.
       @param weights an edge attribute map giving graph edges weights.
       @param m a NodeMatrix in which to store shortest path distances
       from node <tt>i</tt> to node <tt>j</tt> in cell <tt>(i,j)</tt>.
       @param adaptor a shortests path adaptor providing addition and
       comparison for the type <tt>T</tt>.
     */
    public static <T> void apsp(Digraph dg,
                                EdgeMap<T> weights,
                                NodeMatrix<T> m,
                                NumAdaptor<T> adaptor)
    {
        if (dg.edgeSize() > dg.nodeSize() * Math.log(dg.nodeSize()))
            apspDense(dg, weights, m, adaptor);
        else
            apspSparse(dg, weights, m, adaptor);
    }

    /**
       Compute all pairs shortests paths distances using Johnson's
       algorithm.
       First, a copy of the input graph is made and the copy is
       augmented with a new source vertex and edges from it 
       to every other node with weight zero.  Then {@link #apspSparse}
       is called with new source vertex.

       @param digraph the digraph whose shortests path distances are to be 
       computed.
       @param weights an edge attribute map giving graph edges weights.
       @param m a NodeMatrix in which to store shortest path distances
       from node <tt>i</tt> to node <tt>j</tt> in cell <tt>(i,j)</tt>.
       @param adaptor a shortests path adaptor providing addition and
       comparison for the type <tt>T</tt>.
       @return a Path containing a negative cycle in dg, or null
       if the computation is successful.
    */
    public static <T> Path apspSparse(Digraph digraph,
                                      EdgeMap<T> weights,
                                      NodeMatrix<T> m,
                                      NumAdaptor<T> adaptor)
    {
        /*
          Make a copy of digraph, and augment it with a new source
          vertex which can reach every other node via an edge with
          zero weight.
         */
        BasicDigraph dg = BasicDigraph.copy(digraph);
        Node source = dg.genNode();
        T zero = adaptor.zero();
        for (Node n : dg.nodes()) {
            if (n != source) {
                Edge e = dg.genEdge(source, n);
                weights.set(e, zero);
            }
        }
        return apspSparse(dg, source, weights, m, adaptor);
    }


    /**
       Compute all pairs shortests paths distances using Johnson's
       algorithm.
       First a potential function is found using {@link SSSP#arbw}.
       If this is successful, then the potential function is used
       to reduce the single source shortests path problem for arbitrary
       weights to the single source shortests path problem for
       positive edge weights. Subsequently, {@link SSSP#dijkstra}
       is called for every node.<br></br>       
       Complexity: <tt>O(nm + n<sup>2</sup>log n)</tt>.

       @param dg the digraph whose shortests path distances are to be 
       computed.
       @param source a vertex which can reach every other vertex in <tt>dg</tt>.
       @param weights an edge attribute map giving graph edges weights.
       @param m a NodeMatrix in which to store shortest path distances
       from node <tt>i</tt> to node <tt>j</tt> in cell <tt>(i,j)</tt>.
       @param adaptor a shortests path adaptor providing addition and
       comparison for the type <tt>T</tt>.
       @return a Path containing a negative cycle in dg, or null
       if the computation is successful.
    */
    public static <T> Path apspSparse(Digraph dg,
                                      Node source,
                                      EdgeMap<T> weights,
                                      NodeMatrix<T> m,
                                      NumAdaptor<T> adaptor)
    {
        /*
          Do SSSP from the new source.
         */
        NodeMap<T> potential = dg.createNodeMap(null);
        Path nc = SSSP.arbw(dg, source, potential, weights, adaptor);
        if (nc != null) {
            return nc;
        }
        /*
          Use the distmap (potential) to adapt all edge weights to positive
          values in such a manner as to preserve shortests paths.
         */
        EdgeSource<T> posWeights = new PosWeightEdgeMap<T>(potential, weights, adaptor);
        NodeMap<Edge> predMap = dg.createNodeMap(null);
        for (Node n : dg.nodes()) {
            NodeMapFromMatrix<T> nMap = new NodeMapFromMatrix<T>(n, potential, m, adaptor);
            SSSP.dijkstra(dg, n, nMap, posWeights, predMap, adaptor);
        }
        return null;
    }

    /*
      Adapt edge weights to positive edge weights with potential function.
     */
    private static class PosWeightEdgeMap<T> implements EdgeSource<T>
    {
        protected NodeMap<T> potential;
        protected NumAdaptor<T> adaptor;
        protected EdgeMap<T> weights;
        PosWeightEdgeMap(NodeMap<T> pot, EdgeMap<T> weights, NumAdaptor<T> ad)
        {
            potential = pot;
            adaptor = ad;
            this.weights = weights;
        }

        public T get(int i)
        {
            // only used by Dijkstra which always calls .get(Edge), and access by int 
            // doesn't help much in this case...
            throw new IllegalStateException();
        }

        public T get(Edge e)
        {
            T sp = potential.get(e.source());
            T tp = potential.get(e.target());
            T ew = weights.get(e);
            T s = adaptor.add(sp, ew);
            return adaptor.subtract(s, tp);
        }

        public void grow(int cap) {}
        public void shrink(int cap, int[] perm) {}
        public void clear() {}
    }

    /**
       A node map which is just a row of a node matrix.
       In addition, while the matrix may store negative
       values, the node map only reports and sets positive
       values.
       A value for a node n in the nodemap representing distances 
       from node s is
       m_sn + pot(n) - pot(s)
       where m_sn is the matrix entry for node pair (s,n).
     */
    private static class NodeMapFromMatrix<T> implements NodeMap<T>
    {
        protected Node node;
        protected NodeMatrix<T> m;
        protected NodeMap<T> potential;
        protected NumAdaptor<T> adaptor;

        NodeMapFromMatrix(Node n, NodeMap<T> pot, NodeMatrix<T> m, NumAdaptor<T> adaptor)
        {
            this.node = n;
            this.m = m;
            this.potential = pot;
        }

        public T get(Node n)
        {
            return adaptor.add(adaptor.subtract(m.get(node, n),
                                                potential.get(node)),
                               potential.get(n));
        }

        public T set(Node n, T v)
        {
            T realVal = adaptor.subtract(adaptor.add(potential.get(node), v),
                                         potential.get(n));
            return m.set(node, n, realVal);
        }
        
        /*
          Do nothing, it will be called for the underlying NodeMatrix/potential
          if required anyway..
         */
        public void grow(int cap) {}
        public void shrink(int cap, int[] perm) {}
        public void clear() {}

        /*
          Don't support get by int.
         */
        public T get(int i)
        {
            throw new IllegalStateException();
        }

        public T set(int i, T v)
        {
            throw new IllegalStateException();
        }
    }

    /**
       Compute all pairs shortests paths with Floyd-Warshall algorithm.
       For every pair of nodes <tt>(i,j)</tt>, compute the distance
       of the shortest path from <tt>i</tt> to <tt>j</tt>, storing
       the result in a node matrix.  The algorithm runs in  <tt>O(n<sup>3</sup>)</tt>. 
       If shortests paths are not well defined due to the existance
       of a negative cycle, the matrix storing the shortests paths 
       distances will contain an entry <tt>(i,i)</tt> whose value
       is negative for some node <tt>i</tt>.

       @param dg the digraph whose shortests path distances are to be 
       computed.
       @param weights an edge attribute map giving graph edges weights.
       @param m a NodeMatrix in which to store shortest path distances
       from node <tt>i</tt> to node <tt>j</tt> in cell <tt>(i,j)</tt>.
       @param adaptor a shortests path adaptor providing addition and
       comparison for the type <tt>T</tt>.
     */
    public static <T> void apspDense(Digraph dg,
                                     EdgeSource<T> weights,
                                     NodeMatrix<T> m,
                                     NumAdaptor<T> adaptor)
    {

        /*
          Ininitialize the matrix so that each edge (i,j) in the graph
          has an entry with the weight from the weight edge map and
          all other entries have adaptor.inf()
         */
        for (Node i : dg.nodes()) {

            for (Node j : dg.nodes()) {
                m.set(i, j, adaptor.inf());
            }
            m.set(i, i, adaptor.zero());
            for (Edge e = i.out(); e != null; e = e.next()) {
                m.set(i, e.target(), weights.get(e));
            }
        }
        /*
          Floyd-Warshallize.
         */
        for (Node i : dg.nodes()) {
            for (Node j : dg.nodes()) {
                T dij = m.get(i, j);
                for (Node k : dg.nodes()) {
                    T djk = m.get(j, k);
                    T dik = m.get(i, k);
                    T sik = adaptor.add(dij, djk);
                    if (adaptor.compare(sik, dik) < 0) {
                        m.set(i, k, sik);
                    }
                }
            }
        }
    }

    /**
       Incrememtal APSP for dense graphs.

       Compute incrementally all pairs shortests paths for a dense graph,
       provided with an initial node matrix containing all pairs shortests paths
       distances and an edge whose weight has decreased.  Before a call
       to this function, the new edge weight is not reflected in the 
       node matrix.  This method updates the matrix to reflect the reduced
       edge weight in <tt>O(n<sup>2</sup>)</tt> time.

       @param dg the digraph whose shortests path distances are to be 
       updated.
       @param e an edge whose weight has been reduced in <tt>weights</tt>.
       @param weights an edge attribute map giving graph edges weights.
       @param m a NodeMatrix in which shortest paths distances from 
       from node <tt>i</tt> to node <tt>j</tt> in cell <tt>(i,j)</tt> are stored.
       The matrix should contain the distances prior to a decrease in edge
       weight for edge <tt>e</tt>.
       @param adaptor a shortests path adaptor providing addition and
       comparison for the type <tt>T</tt>.
       
     */
    public static <T> void dynApspDense(Digraph dg,
                                        Edge e,
                                        EdgeSource<T> weights,
                                        NodeMatrix<T> m,
                                        NumAdaptor<T> adaptor)
    {
        Node s = e.source();
        Node t = e.target();
        T w = weights.get(e);
        if (adaptor.compare(m.get(s, t), w) < 0) {
            return;
        }
        for (Node i : dg.nodes()) {
            T mis = m.get(i, s);
            for (Node j : dg.nodes()) {
                T mij = m.get(i,j);
                T mtj = m.get(t, j);
                T sij = adaptor.add(mis, adaptor.add(w, mtj));
                if (adaptor.compare(sij, mij) < 0) {
                    m.set(i, j, sij);
                }
            }
        }
    }
}
