package stixar.graph.search;

import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.util.CList;
import stixar.util.PQueue;
import stixar.util.BinaryPQ;

import java.util.Comparator;

/**
   A search method which works by breadth first search, but
   is intended for exploring graphs too large to fit in memory.
   <p>
   KBestBFS performs breadth first search from a source node
   without maintaining any data structures relevant to all
   nodes or edges in a graph.  Consequently, this version of 
   BFS may revisit vertices without knowing it.  This type
   of graph search is referred to as <em>exploration</em> in 
   the stixar graph library.
   </p>
   <p>
   KBestBFS assumes that the maximum degree of any node 
   is sufficiently small that all neighbors of a given 
   node may fit into memory.  KBestBFS proceeds by finding
   all outgoing or adjacent edges to a node <tt>u</tt>, then selecting
   the <tt>k</tt> best edges <tt>(u,v)</tt> to continue the search
   along each such <tt>v</tt> recursively.  The number <tt>k</tt>
   should be sufficiently small that <tt>k<sup>2</sup></tt> fits
   into memory.
   </p>
 */
public class KBestBFS
{

    protected PQueue<Edge> pq;
    protected int k;
    
    public KBestBFS(PQueue<Edge> pq, int k)
    {
        this.pq = pq;
        this.k = k;
    }
    
    public KBestBFS(Comparator<Edge> cmp, int k)
    {
        this.pq = new BinaryPQ(cmp, 1024);
        this.k = k;
    }

    public void onEdge(Edge e)
    {}

    public void onEnqueue(Node n)
    {}

    public void onDequeue(Node n)
    {}

    public void onSelect(Edge e)
    {}

    public void explore(Node n)
    {
        CList<Node> queue = new CList<Node>();
        queue.addFirst(n);
        while (!queue.isEmpty()) {
            n = queue.removeFirst();
            onDequeue(n);
            pq.clear();
            for (Edge e = n.out(); e != null; e = e.next()) {
                onEdge(e);
                pq.insert(e);
            }
            for (int i=0; i<k; ++i) {
                Edge e = pq.extractMin();
                onSelect(e);
                Node t = e.target();
                queue.addLast(t);
                onEnqueue(t);
            }
        }
    }
}