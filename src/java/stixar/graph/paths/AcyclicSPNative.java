package stixar.graph.paths;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Digraph;

import stixar.graph.attr.NativeMap;
import stixar.graph.attr.NativeNodeMap;
import stixar.graph.attr.NativeEdgeMap;
import stixar.graph.attr.IntNodeMap;
import stixar.graph.attr.FloatNodeMap;
import stixar.graph.attr.DoubleNodeMap;
import stixar.graph.attr.LongNodeMap;
import stixar.graph.attr.IntEdgeMap;
import stixar.graph.attr.FloatEdgeMap;
import stixar.graph.attr.DoubleEdgeMap;
import stixar.graph.attr.LongEdgeMap;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.EdgeSource;

import stixar.graph.GraphFilter;

import stixar.util.CList;


/**
   Shortests paths in acyclic digraphs with native attributes.
 */
public class AcyclicSPNative extends AcyclicSPBase // in AcyclicSP.java
{
    protected NativeNodeMap distMap;
    protected NativeEdgeMap weights;
    
    public AcyclicSPNative(Digraph dg, 
                           Node source, 
                           Node target, 
                           NativeNodeMap distMap, 
                           NativeEdgeMap weights,
                           NodeMap<Edge> parents)
    {
        super(dg, source, target, parents);
        this.distMap = distMap;
        this.weights = weights;
    }
    
    /*
      Javadoc'd in Algorithm.
    */
    public void run()
    {
        reset();
        /*
          Get top sort, possibly rooted at source.
        */
        if (source != null)
            dfs.run();
        else
            dfs.visit(source);
        
        /*
          
        */
        switch (distMap.type()) {
        case Int:
            IntNodeMap idm = (IntNodeMap) distMap;
            IntEdgeMap iem = (IntEdgeMap) weights;
            for (Node n : tsortList) {
                if (filter != null && filter.filter(n)) continue;
                if (n == target) break;
                int nDist = idm.get(n);
                if (nDist == Integer.MAX_VALUE) continue;
                for (Edge e = n.out(); e != null; e = e.next()) {
                    if (filter != null && filter.filter(e)) continue;
                    int weight = iem.get(e);
                    int newDist = nDist + weight;
                    Node t = e.target();
                    int tDist = idm.get(t);
                    if (newDist < tDist) {
                        idm.set(t, newDist);
                        t.set(parents, e);
                    }
                }
            }
            break;
        case Float:
            FloatNodeMap fdm = (FloatNodeMap) distMap;
            FloatEdgeMap fem = (FloatEdgeMap) weights;
            for (Node n : tsortList) {
                if (filter != null && filter.filter(n)) continue;
                if (n == target) break;
                float nDist = fdm.get(n);
                if (nDist == Float.POSITIVE_INFINITY) continue;
                for (Edge e = n.out(); e != null; e = e.next()) {
                    if (filter != null && filter.filter(e)) continue;
                    float weight = fem.get(e);
                    float newDist = nDist + weight;
                    Node t = e.target();
                    float tDist = fdm.get(t);
                    if (newDist < tDist) {
                        fdm.set(t, newDist);
                        t.set(parents, e);
                    }
                }
            }
            break;
        case Long:
            LongNodeMap ldm = (LongNodeMap) distMap;
            LongEdgeMap lem = (LongEdgeMap) weights;
            for (Node n : tsortList) {
                if (filter != null && filter.filter(n)) continue;
                if (n == target) break;
                long nDist = ldm.get(n);
                if (nDist == Long.MAX_VALUE) continue;
                for (Edge e = n.out(); e != null; e = e.next()) {
                    if (filter != null && filter.filter(e)) continue;
                    long weight = lem.get(e);
                    long newDist = nDist + weight;
                    Node t = e.target();
                    long tDist = ldm.get(t);
                    if (newDist < tDist) {
                        ldm.set(t, newDist);
                        t.set(parents, e);
                    }
                }
            }
            break;
        case Double:
            DoubleNodeMap ddm = (DoubleNodeMap) distMap;
            DoubleEdgeMap dem = (DoubleEdgeMap) weights;
            for (Node n : tsortList) {
                if (filter != null && filter.filter(n)) continue;
                if (n == target) break;
                double nDist = ddm.get(n);
                if (nDist == Double.MAX_VALUE) continue;
                for (Edge e = n.out(); e != null; e = e.next()) {
                    if (filter != null && filter.filter(e)) continue;
                    double weight = dem.get(e);
                    double newDist = nDist + weight;
                    Node t = e.target();
                    double tDist = ddm.get(t);
                    if (newDist < tDist) {
                        ddm.set(t, newDist);
                        t.set(parents, e);
                    }
                }
            }
            break;
        default:
            throw new IllegalStateException();
        }
    }
    
    protected void reset()
    {
        super.reset();
        switch (distMap.type()) {
        case Int:
            IntNodeMap imap = (IntNodeMap) distMap;
            for (Node n : digraph.nodes()) {
                if (n == source)
                    imap.set(n, 0);
                else
                    imap.set(n, Integer.MAX_VALUE);
            }
            break;
        case Float:
            FloatNodeMap fmap = (FloatNodeMap) distMap;
            for (Node n : digraph.nodes()) {
                if (n == source)
                    fmap.set(n, 0f);
                else
                    fmap.set(n, Float.POSITIVE_INFINITY);
            }
            break;
            
        case Long:
            LongNodeMap lmap = (LongNodeMap) distMap;
            for (Node n : digraph.nodes()) {
                if (n == source)
                    lmap.set(n, 0l);
                else
                    lmap.set(n, Long.MAX_VALUE);
            }
            break;
        case Double:
            DoubleNodeMap dmap = (DoubleNodeMap) distMap;
            for (Node n : digraph.nodes()) {
                if (n == source)
                    dmap.set(n, 0d);
                else
                    dmap.set(n, Double.POSITIVE_INFINITY);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
}
