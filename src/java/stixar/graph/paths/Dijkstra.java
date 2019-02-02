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

import stixar.util.BinaryPQ;
import stixar.util.PQueue;
import stixar.util.Cell;
import stixar.util.NumAdaptor;
import stixar.util.fheap.FibHeap;
import stixar.util.fheap.FHeapCell;

import java.util.Comparator;

/**
   Dijkstra's shortest paths for generically attributed graphs.
*/
public class Dijkstra<T> extends DijkstraBase
{
    protected NodeMap<T> distMap;
    protected EdgeSource<T> weights;
    protected NumAdaptor<T> adaptor;
    
    public Dijkstra(Graph dg, 
                    Node source,
                    Node target,
                    NodeMap<Edge> parents,
                    NodeMap<T> distMap,
                    EdgeSource<T> weights,
                    NumAdaptor<T> adaptor,
                    NodeMap<Cell<Node>> cells)
    {
        super(dg, source, target, parents, cells);
        this.distMap = distMap;
        this.weights = weights;
        this.adaptor = adaptor;
        this.pQueue = new BinaryPQ<Node>(getComparator(distMap, adaptor), dg.nodeSize());
        //this.pQueue = new FibHeap<Node>(getComparator(distMap, adaptor));
    }
    
    protected Comparator<Node> getComparator(final NodeMap<T> dMap, 
                                             final NumAdaptor<T> adaptor)
    {
        return new Comparator<Node>()
        {
            public int compare(Node n1, Node n2)
            {
                return adaptor.compare(n1.get(dMap), n2.get(dMap));
            }
        };
    }

    protected void reset()
    {
        pQueue.clear();
        pqItems.clear();
    }
    
    public void run()
    {
        reset();
        pqItems.set(source, pQueue.insert(source));
        while(!pQueue.isEmpty()) {
            Node s = pQueue.extractMin();
            if (s == target) {
                pQueue.clear();
                break;
            }
            T sDist = distMap.get(s);
            for (Edge e = s.out(); e != null; e = e.next()) {
                // implement filtering.
                if (filter != null && filter.filter(e)) 
                    continue;
                T eWeight = weights.get(e);
                Node t = e.target();
                T tDist = adaptor.add(sDist, eWeight);
                if (pqItems.get(t) == null) {
                    distMap.set(t, tDist);
                    parents.set(t, e);
                    pqItems.set(t, pQueue.insert(t));
                } else {
                    if (adaptor.compare(tDist, distMap.get(t)) < 0) {
                        distMap.set(t, tDist);
                        parents.set(t, e);
                        pQueue.requeue(pqItems.get(t));
                    }
                }
            }
        }
    }
}
