package stixar.graph.paths;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.attr.IntEdgeMap;
import stixar.graph.attr.IntNodeMap;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.NodeMap;

import stixar.util.NumAdaptor;

import java.util.Random;

import junit.framework.TestCase;

public class DijkstraTest extends TestCase
{
    public DijkstraTest()
    {
        super("DijkstraTest");
    }

    public void testNative()
    {
        int nSize = 5000;
        int eSize = 50000;
        BasicDGFactory f = new BasicDGFactory(nSize, eSize);
        f.genNodes(nSize);
        f.genEdges(eSize);
        BasicNode source = f.genSource();
        BasicDigraph g = f.digraph();
        // managed.
        IntEdgeMap w = g.createIntEdgeMap();
        // ad-hoc, unmanaged.
        IntNodeMap dist = new IntNodeMap(new int[g.nodeAttrSize()]);
        Random rnd = new Random(0);
        for (Edge e : g.edges()) 
            w.set(e, rnd.nextInt(100));
        long start = System.currentTimeMillis();
        SSSP.dijkstra(g, source,  dist, w);
        long end = System.currentTimeMillis();
        
        /* for (Node n : g.nodes())
        System.out.println(n + ": " + n.getInt(dist));*/
        System.out.println("native actual computation took " + (end - start) + " milliseconds.");
    }


    public void testGeneric()
    {
        int nSize = 5000;
        int eSize = 50000;
        BasicDGFactory f = new BasicDGFactory(nSize, eSize);
        f.genNodes(nSize);
        f.genEdges(eSize);
        BasicNode source = f.genSource();
        BasicDigraph g = f.digraph();
        // managed.
        EdgeMap<Integer> w = g.createEdgeMap(new Object());
        NodeMap<Integer> dist = g.createNodeMap(new Object());
        Random rnd = new Random(0);
        for (Edge e : g.edges()) 
            w.set(e, rnd.nextInt(100));
        long start = System.currentTimeMillis();
        SSSP.dijkstra(g, source,  dist, w, NumAdaptor.Int);
        long end = System.currentTimeMillis();
        
        /*for (Node n : g.nodes())
          System.out.println(n + ": " + n.getInt(dist));*/
        System.out.println("generic actual computation took " + (end - start) + " milliseconds.");
    }

    public static void main(String[] args)
    {
        DijkstraTest t = new DijkstraTest();
        t.testNative();
        t.testGeneric();
    }
}
