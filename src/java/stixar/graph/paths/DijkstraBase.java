package stixar.graph.paths;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Graph;
import stixar.graph.GraphFilter;

import stixar.graph.attr.EdgeSource;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.NodeSink;

import stixar.graph.attr.ArrayNodeMap;

import stixar.util.PQueue;
import stixar.util.Cell;

import stixar.util.fheap.FibHeap;
import stixar.util.fheap.FHeapCell;

import java.util.Comparator;

/**
   Diskstra's single source shortest path algorithm.

   Dijkstra's algorithm is a single source shortest path
   algorithm working on directed graphs with non-negative 
   edge weights.  If the graph is acyclic, {@link AcyclicSP}
   supplies a faster algorithm.
 */
class DijkstraBase
{
    NodeMap<Edge> parents;
    Graph graph;
    Node source;
    Node target;
    NodeMap<Cell<Node>> pqItems;
    PQueue<Node> pQueue;
    GraphFilter filter;
    
    @SuppressWarnings("unchecked")
    protected DijkstraBase(Graph graph,
                           Node source,
                           Node target,
                           NodeMap<Edge> pMap,
                           NodeMap<Cell<Node>> cMap)
    {
        this.graph = graph;
        this.source = source;
        this.target = target;
        int ansz = graph.nodeAttrSize();
        if (pMap == null) 
            this.parents = new ArrayNodeMap<Edge>(new Edge[ansz]);
        else
            this.parents = pMap;
        if (cMap == null) 
            this.pqItems = new ArrayNodeMap<Cell<Node>>(new Cell[ansz]);
        else
            this.pqItems = cMap;
        this.filter = graph.getFilter();
    }
                         
    /**
       Allow setting the priority queue for this dijkstra 
       instance.
       <p>
       <b>Note:</b><br></br>
       The priority queue not only defines the data structure 
       in which node distances are managed, but also defines
       the comparator for a node.  Hence, one can use this
       method to define a priority queue which implements 
       <tt>A<sup>*</sup></tt> search with a heuristic function 
       <tt>h</tt>.  The queue should order the priorities by
       the sum of the distance map and the heuristic for each
       node.
       </p>
       @param pq a priority queue.
    */
    public void setPQueue(PQueue<Node> pq)
    {
        pQueue = pq;
    }
    
    /**
       Return a NodeMap describing the shortest path tree.
       @return a NodeMap giving for each node its parent
       in the shortest paths tree, or <tt>null</tt> if the
       node is not reachable from the source.
    */
    public NodeMap<Edge> parents()
    {
        return parents;
    }
    
    /**
       Return the current source node for this algorithm.
    */
    public Node source()
    {
        return source;
    }
    
    /**
       Return the current target node for this algorithm.
    */
    public Node target()
    {
        return target;
    }
    
    /**
       Set and return the current source node.
       @return n
    */
    public Node source(Node n)
    {
        return source = n;
    }
    
    /**
       Set and return the current target node.
       @return n
    */
    public Node target(Node n)
    {
        return target = n;
    }
}
