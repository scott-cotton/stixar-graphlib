package stixar.graph;

import junit.framework.TestCase;

import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.NodeMap;

import stixar.util.CList;
import stixar.util.Pair;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;


public class BasicDigraphTest extends TestCase
{

    public BasicDigraphTest()
    {
        super("BasicDigraph");
    }

    public void testCreate()
    {
        BasicDigraph g = new BasicDigraph(100, 500);
        g = new BasicDigraph();
    }

    public void testToString()
    {
        BasicDigraph g = new BasicDigraph(100, 500);
        System.out.println("empty:\n" + g);
        g.genNodes(100);
        System.out.println("100 nodes:\n" + g);

        for (int i=0; i<100; ++i) {
            g.genEdge(g.node(i), g.node(i));
        }
        System.out.println("100 nodes self loop:\n" + g);
    }

    public void testGrow()
    {
        BasicDigraph g = new BasicDigraph();
        for (int i=0; i<16385; ++i)
            g.genNode();
        for (int i=0; i<16384; ++i)
            g.genEdge(g.node(i), g.node(i));
    }

    public void testMoveEdge()
    {
        int nSize = 10;
        BasicDigraph g = new BasicDigraph();
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i) {
            BasicNode u = g.node(i-1);
            BasicNode v = g.node(i);
            BasicEdge e = g.genEdge(u, v);
        }
        CList<Edge> le = new CList<Edge>();
        for (Edge e : g.edges()) {
            le.add(e);
        }
        for (Edge e : le) {
            g.moveEdge(e, e.target(), e.source());
        }
        for (Node n : g.nodes()) {
            for (Edge e = n.out(); e != null; e = e.next()) {
                assertTrue(e.source().nodeId() == e.target().nodeId() + 1);
            }
        }
    }

    public void testConcurrentMod()
    {
        int nSize = 10;
        BasicDigraph g = new BasicDigraph();
        g.genNodes(nSize);
        try {
            for (Node n : g.nodes()) {
                // ok to alter edges.
                g.genEdge(n, n);
            }
        } catch (ConcurrentModificationException e) {
            assertTrue(false);
        }
        try {
            for (Node n : g.nodes()) {
                g.remove(n);
            }
            assertTrue(false);
        } catch (ConcurrentModificationException e) {
        }
        try {
            for (Edge e : g.edges()) {
                // ok to add or delete nodes, so long
                // as no edges are changed/deleted as a consequence.
                g.genNode();
            }
        } catch (ConcurrentModificationException e) {
            assertTrue(false);
        }
        try {
            for (Edge e : g.edges()) {
                g.remove(e);
            }
            assertTrue(false);
        } catch (ConcurrentModificationException e) {
        }
    }

    public void testIterIO()
    {
        int nSize = 10;
        BasicDigraph g = new BasicDigraph();
        g.genNodes(nSize);
        for (Node n : g.nodes())
            g.genEdge(n, n);
        for (int i=1; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
        }
        for (Node n : g.nodes()) {
            BasicNode bn = (BasicNode) n;
            System.out.println(bn);
            for (Edge e = bn.out(); e != null; e = e.next()) {
                assertTrue(e.source() == n);
                System.out.println("\t(out) " + e);
            }
            for (BasicEdge e = bn.in(); e != null; e = e.nextIn()) {
                assertTrue(e.target() == n);
                System.out.println("\t(in) " + e);
            }
        }
    }

    public void testRemoveEdge()
    {
        int nSize = 10;
        BasicDigraph g = new BasicDigraph();
        g.genNodes(nSize);
        for (Node n : g.nodes())
            g.genEdge(n, n);
        CList<Edge> le = new CList<Edge>();
        for (Edge e : g.edges()) {
            le.add(e);
        }
        for (Edge e : le) {
            g.remove(e);
        }
        assertTrue(g.edgeSize() == 0);
        for (Edge e : g.edges()) 
            assertTrue(false);
        for (Edge e : le) {
            assertTrue(e.edgeId() < 0);
            assertTrue(e.source().nodeId() == e.target().nodeId());
            assertTrue(e.source().nodeId() >= 0);
        }
    }


    public void testRemoveNode()
    {
        int nSize = 128;
        BasicDigraph g = new BasicDigraph(2,1024);
        g.genNodes(nSize);
        for (Node n : g.nodes())
            g.genEdge(n, n);
        for (int i=1; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
        }

        CList<Node> ln = new CList<Node>();
        for (Node n : g.nodes()) {
            BasicNode bn = (BasicNode) n;
            ln.add(n);
        }
        int eSize = g.edgeSize();
        System.out.println("edge size: " + eSize);
        assertTrue(eSize == 2 * nSize - 1);
        for (Node n : ln) {
            System.out.println("removing " + n);
            BasicNode bn = (BasicNode) n;
            g.remove(n);
            assertTrue(g.nodeSize() == nSize - 1);
            nSize -= 1;
            assertTrue(n.nodeId() == -1);
            assertTrue(n.out() == null);
            assertTrue(bn.degree() == 0);
            for (Edge e = bn.in(); e != null; e = e.next()) {
                System.out.println("Still in inlist: " + e);
                assertTrue(false);
            }
        }
        for (Node n : g.nodes())
            assertTrue(false);
    }

    public void testShrinkEdges()
    {
        int nSize = 10;
        BasicDigraph g = new BasicDigraph(10,1);
        g.genNodes(nSize);
        for (Node n : g.nodes())
            g.genEdge(n, n);
        for (int i=1; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
            g.genEdge(g.node(i-1), g.node(i));
        }
        CList<Edge> edges = new CList<Edge>();
        for (Edge e : g.edges()) {
            edges.add(e);
        }
        for (Edge e : edges) {
            g.remove(e);
        }
    }


    public void testGrowAndShrinkEdgeAttrs()
    {
        int nSize = 16;
        BasicDigraph g = new BasicDigraph(1,1);
        g.genNodes(nSize);
        HashMap<Pair<Node,Node>,Integer> eMap = new HashMap<Pair<Node,Node>,Integer>();
        EdgeMap<Integer> mgdEdgeMap = g.createEdgeMap("fred");
        Random rnd = new Random(0);
        for (Node n : g.nodes()) {
            Edge e = g.genEdge(n, n);
            int v = rnd.nextInt(100);
            eMap.put(new Pair<Node,Node>(n,n), v);
            mgdEdgeMap.set(e, v);
        }
        for (int i=1; i<nSize; ++i) {
            Node u = g.node(i);
            Node v = g.node(i-1);
            
            Edge e = g.genEdge(u, v);
            int iv = rnd.nextInt(100);
            eMap.put(new Pair<Node,Node>(u,v), iv);
            mgdEdgeMap.set(e, iv);

            e = g.genEdge(v, u);
            iv = rnd.nextInt(100);
            eMap.put(new Pair<Node,Node>(v,u), iv);
            mgdEdgeMap.set(e, iv);
        }
        // after growing, are the maps still in sync?
        for (Edge e : g.edges()) {
            int mgdv = mgdEdgeMap.get(e);
            int v = eMap.get(new Pair<Node,Node>(e.source(), e.target()));
            assertTrue(mgdv == v);
        }
        
        CList<Edge> edges = new CList<Edge>();
        for (Edge e : g.edges()) {
            edges.add(e);
        }
        for (Edge e : edges) {
            if (rnd.nextBoolean()) g.remove(e);
            else if (rnd.nextBoolean()) g.remove(e);
            else if (rnd.nextBoolean()) g.remove(e);
        }
        // now we've shrunk.
        for (Edge e : g.edges()) {
            int mgdv = mgdEdgeMap.get(e);
            int v = eMap.get(new Pair<Node,Node>(e.source(), e.target()));
            assertTrue(mgdv == v);
        }
    }

    /*
      The following test requires that the 
      call to _maybeShrinkEdges in BasicDigraph's
      remove(Node) method be commented out.

      I don't know how to test this otherwise..
    */
    /*
    public void testGrowAndShrinkNodeAttrs()
    {
        int nSize = 16;
        BasicDigraph g = new BasicDigraph(1,1024);
        g.genNodes(nSize);
        
        HashMap<Edge,Integer> nMap = new HashMap<Edge,Integer>();
        NodeMap<Integer> mgdNodeMap = g.createNodeMap("fred");
        Random rnd = new Random(0);
        for(Node n : g.nodes()) {
            Edge e = g.genEdge(n,n);
            int v = rnd.nextInt(100);
            nMap.put(e, v);
            mgdNodeMap.set(n, v);
        }
        for (int i=0; i<nSize * 2; ++i) {
            Node n = g.genNode();
            Edge e = g.genEdge(n,n);
            int v = rnd.nextInt(100);
            nMap.put(e, v);
            mgdNodeMap.set(n, v);
        }
        //
        //
        // 
        CList<Node> nodeList = new CList<Node>();
        for (Node n: g.nodes()) {
            // should be there, all nodes have one edge.
            Edge e = n.out();
            int mv = nMap.get(e);
            int mgdv = mgdNodeMap.get(n);
            assertTrue(mv == mgdv);
            nodeList.add(n);
        }
        System.out.println(nodeList);
        System.out.println("keepers: ");
        for (Node n : nodeList) {
            if (rnd.nextBoolean() || rnd.nextBoolean()) {
                System.out.println(" => remove node " + n);
                g.remove(n);
            } else {
                System.out.println(" => keep node " + n);
            }
        }
        //
        // we're pretty sure to have shrunk now, the
        // nodes have different ids, but the edges
        // coming out of them have the same ids
        //
        for (Node n : g.nodes()) {
            // should be there, all nodes have one edge.
            System.out.println("still here: " + n);
            Edge e = n.out();
            System.out.println("edge is " + e);
            int mv = nMap.get(e);
            int mgdv = mgdNodeMap.get(n);
            assertTrue(mv == mgdv);
        }
    }
    */

    public static void main(String[] args)
    {
        BasicDigraphTest t = new BasicDigraphTest();
        t.testCreate();
        t.testToString();
        t.testGrow();
        t.testMoveEdge();
        t.testConcurrentMod();
        t.testRemoveEdge();
        t.testIterIO();
        t.testRemoveNode();
        t.testShrinkEdges();
        t.testGrowAndShrinkEdgeAttrs();
        // see comment above the method. t.testGrowAndShrinkNodeAttrs();
    }
}
