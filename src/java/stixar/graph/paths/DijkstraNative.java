package stixar.graph.paths;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Graph;

import stixar.graph.attr.EdgeSource;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.NodeSink;

import stixar.graph.attr.NativeMap;
import stixar.graph.attr.NativeEdgeMap;
import stixar.graph.attr.NativeNodeMap;
import stixar.graph.attr.IntEdgeMap;
import stixar.graph.attr.IntNodeMap;
import stixar.graph.attr.FloatEdgeMap;
import stixar.graph.attr.FloatNodeMap;
import stixar.graph.attr.DoubleEdgeMap;
import stixar.graph.attr.DoubleNodeMap;
import stixar.graph.attr.LongEdgeMap;
import stixar.graph.attr.LongNodeMap;
import stixar.graph.attr.ArrayNodeMap;


import stixar.util.PQueue;
import stixar.util.Cell;
import stixar.util.BinaryPQ;

import stixar.util.fheap.FibHeap;

import java.util.Comparator;

/**
   Dijkstra's shortest paths for natively attributed graphs.
   
   Available native numeric types: <tt>int,long,float,double</tt>.
*/
public class DijkstraNative extends DijkstraBase
{
    NativeNodeMap distMap;
    NativeEdgeMap weights;
    
    public DijkstraNative(Graph dg, 
                          Node source,
                          Node target,
                          NodeMap<Edge> parents,
                          NativeNodeMap distMap,
                          NativeEdgeMap weights,
                          NodeMap<Cell<Node>> cells)
    {
        super(dg, source, target, parents, cells);
        this.distMap = distMap;
        this.weights = weights;
        //this.pQueue = new FibHeap<Node>(getComparator(distMap, distMap.type()));
        this.pQueue = new BinaryPQ<Node>(getComparator(distMap, distMap.type()), dg.nodeSize());
    }
    

    final void reset()
    {
        pQueue.clear();
        pqItems.clear();
    }
    
    
    public final void run()
    {
        reset();
        pqItems.set(source, pQueue.insert(source));
        switch (distMap.type()) {
        case Int:
            // 
            IntNodeMap iDists = (IntNodeMap) distMap;
            IntEdgeMap iWeights = (IntEdgeMap) weights;
            while(!pQueue.isEmpty()) {
                Node s = pQueue.extractMin();
                if (s == target)
                    break;
                int sDist = iDists.get(s);
                for (Edge e = s.out(); e != null; e = e.next()) {
                    // implement filtering.
                    if (filter != null && filter.filter(e)) 
                        continue;
                    Node t = e.target();
                    int eWeight = iWeights.get(e);
                    int tDist = sDist + eWeight;
                    if (pqItems.get(t) == null) {
                        iDists.set(t, tDist);
                        pqItems.set(t, pQueue.insert(t));
                        parents.set(t, e);
                    } else if (tDist < iDists.get(t)) {
                        iDists.set(t, tDist);
                        pQueue.requeue(pqItems.get(t));
                        parents.set(t, e);
                    }
                }
            }
            break;
        case Long:
            LongNodeMap lDists = (LongNodeMap) distMap;
            LongEdgeMap lWeights = (LongEdgeMap) weights;
            while(!pQueue.isEmpty()) {
                Node s = pQueue.extractMin();
                if (s == target)
                    break;
                long sDist = lDists.get(s);
                for (Edge e = s.out(); e != null; e = e.next()) {
                    // implement filtering.
                    if (filter != null && filter.filter(e)) 
                        continue;
                    
                    Node t = e.target();
                    long eWeight = lWeights.get(e);
                    long tDist = sDist + eWeight;
                    if (pqItems.get(t) == null) {
                        pqItems.set(t, pQueue.insert(t));
                        lDists.set(t, tDist);
                        parents.set(t, e);
                    } else if (tDist < lDists.get(t)) {
                        lDists.set(t, tDist);
                        pQueue.requeue(pqItems.get(t));
                        parents.set(t, e);
                    }
                }
            }
            break;
        case Float:
            FloatNodeMap fDists = (FloatNodeMap) distMap;
            FloatEdgeMap fWeights = (FloatEdgeMap) weights;
            while(!pQueue.isEmpty()) {
                Node s = pQueue.extractMin();
                if (s == target)
                    break;
                float sDist = fDists.get(s);
                for (Edge e = s.out(); e != null; e = e.next()) {
                    // implement filtering.
                    if (filter != null && filter.filter(e)) 
                        continue;
                    Node t = e.target();
                    float eWeight = fWeights.get(e);
                    float tDist = sDist + eWeight;
                    if (pqItems.get(t) == null) {
                        pqItems.set(t, pQueue.insert(t));
                        fDists.set(t, tDist);
                        parents.set(t, e);
                    } else if (tDist < fDists.get(t)) {
                        fDists.set(t, tDist);
                        pQueue.requeue(pqItems.get(t));
                        parents.set(t, e);
                    }
                }
            }
            break;
            
        case Double:
            DoubleNodeMap dDists = (DoubleNodeMap) distMap;
            DoubleEdgeMap dWeights = (DoubleEdgeMap) weights;
            while(!pQueue.isEmpty()) {
                Node s = pQueue.extractMin();
                if (s == target)
                    break;
                double sDist = dDists.get(s);
                for (Edge e = s.out(); e != null; e = e.next()) {
                    // implement filtering.
                    if (filter != null && filter.filter(e)) 
                        continue;
                    Node t = e.target();
                    double eWeight = dWeights.get(e);
                    double tDist = sDist + eWeight;
                    if (pqItems.get(t) == null) {
                        pqItems.set(t, pQueue.insert(t));
                        dDists.set(t, tDist);
                        parents.set(t, e);
                    } else if (tDist < dDists.get(t)) {
                        dDists.set(t, tDist);
                        pQueue.requeue(pqItems.get(t));
                        parents.set(t, e);
                    }
                }
            }
            break;
        default:
            throw new Error();
        }
    }

    protected Comparator<Node> getComparator(final NativeNodeMap d, NativeMap.Type t)
    {
        switch (t) {
        case Int:
            final IntNodeMap im = (IntNodeMap) d;
            return new Comparator<Node>() {
                public int compare(Node n1, Node n2)
                {
                    int v1 = im.get(n1);
                    int v2 = im.get(n2);
                    return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
                }
            };
        case Float:
            final FloatNodeMap fm = (FloatNodeMap) d;
            return new Comparator<Node>() {
                public int compare(Node n1, Node n2)
                {
                    float v1 = fm.get(n1);
                    float v2 = fm.get(n2);
                    return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
                }
            };
        case Long:
            final LongNodeMap lm = (LongNodeMap) d;
            return new Comparator<Node>() {
                public int compare(Node n1, Node n2)
                {
                    long v1 = lm.get(n1);
                    long v2 = lm.get(n2);
                    return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
                }
            };
        case Double:
            final DoubleNodeMap dm = (DoubleNodeMap) d;
            return new Comparator<Node>() {
                public int compare(Node n1, Node n2)
                {
                    double v1 = dm.get(n1);
                    double v2 = dm.get(n2);
                    return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
                }
            };
        default:
            throw new IllegalArgumentException();
        }
        
    }

}
