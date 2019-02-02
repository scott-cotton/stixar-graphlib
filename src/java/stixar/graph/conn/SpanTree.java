package stixar.graph.conn;

import stixar.graph.Graph;
import stixar.graph.UGraph;
import stixar.graph.Edge;
import stixar.graph.Node;

import stixar.graph.attr.NodeMap;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.NativeEdgeMap;

import stixar.graph.search.DFS;

import stixar.util.CList;
import stixar.util.Partition;
import stixar.util.NumAdaptor;
import stixar.util.fheap.FibHeap;
import stixar.util.fheap.FHeapCell;

import java.util.Comparator;

/**
   Spanning tree computations.
 */
public class SpanTree
{
    // hide from javadoc, this class is never instantiated.
    private SpanTree() {}
    
    /**
       Produce a spanning tree for an undirected graph.
       If the graph is not connected, a forest will be produced.
       @param g the undirected graph.
       @return a node map associating with each node in the graph an edge
       leading to it in the tree, thus defining a tree.
     */
    public static NodeMap<Edge> tree(UGraph g)
    {
        NodeMap<Edge> res = g.createNodeMap((Edge) null);
        DFS dfs = new DFS(g, new DFSVis(res));
        dfs.run();
        return res;
    }


    /**
       Compute a minimum spanning tree for an undirected graph.
       <p>
       A minimum spanning tree is a spanning tree in which the sum
       of the edge weights is minimal amongst all spanning trees.
       If the graph is not connected, a forest will result.
       </p>

       @param g the graph for which to compute a minimum spanning tree.
       @param weights an edge weight map.
       @param adaptor an adaptor defining addition, comparison, zero, and
       "infinity" for the type parameter <tt>T</tt>.
       @return a node map associating with each node in the graph an edge
       leading to it in the tree, thus defining a tree.
     */
    public static <T> CList<Edge> minTree(UGraph g, 
                                          EdgeMap<T> weights, 
                                          NumAdaptor<T> adaptor)
    {
        FibHeap<Edge> pq = new FibHeap<Edge>(new Cmp<T>(weights, adaptor));
        Partition part = new Partition();
        NodeMap<Partition.Block> pMap = g.createNodeMap((Partition.Block) null);
        CList<Edge> tree = new CList<Edge>();
        for (Node n : g.nodes()) {
            pMap.set(n, part.createBlock());
        }
        for (Edge e : g.edges()) pq.insert(e);
        while(!pq.isEmpty() && part.size() > 1) {
            Edge e = pq.extractMin();
            Node s = e.source();
            Node t = e.target();
            Partition.Block sBlock = pMap.get(s);
            Partition.Block tBlock = pMap.get(t);
            if (!sBlock.equals(tBlock)) {
                part.union(sBlock, tBlock);
                tree.add(e);
            }
        }
        return tree;
    }

    /**
       Compute a minimum spanning tree for an undirected graph.
       A minimum spanning tree is a spanning tree in which the sum
       of the edge weights is minimal amongst all spanning trees.

       If the graph is not connected, a forest will result.

       @param g the graph for which to compute a minimum spanning tree.
       @param weights an edge weight map.
       @return a node map associating with each node in the graph an edge
       leading to it in the tree, thus defining a tree.
     */
    public static NodeMap<Edge> minTree(UGraph g,
                                        NativeEdgeMap weights)
    {
        return null;
    }

    /*
      Parametric edge comparator by weight.
     */
    private static final class Cmp<T> implements Comparator<Edge>
    {
        protected NumAdaptor<T> adaptor;
        protected EdgeMap<T> weights;

        public Cmp(EdgeMap<T> w, NumAdaptor<T> a)
        {
            adaptor = a;
            weights = w;
        }

        public final int compare(Edge e1, Edge e2)
        {
            return adaptor.compare(weights.get(e1),
                                   weights.get(e2));
        }
    }


    /*
      Simple DFS visitor for tree() function above.
     */
    private static class DFSVis extends DFS.Visitor
    {
        protected NodeMap<Edge> map;

        DFSVis(NodeMap<Edge> m)
        {
            this.map = m;
        }
            
        public void treeEdge(Edge e)
        {
            map.set(e.target(), e);
        }
    }
}
