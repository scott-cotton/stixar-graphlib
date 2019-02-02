package stixar.graph;

import junit.framework.TestCase;

import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.NodeMap;

import stixar.util.CList;
import stixar.util.Pair;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;


public class BasicUGraphTest extends TestCase
{

    public BasicUGraphTest()
    {
        super("BasicDigraph");
    }

    public void testCreate()
    {
        BasicUGraph g = new BasicUGraph(100, 500);
        g = new BasicUGraph();
    }

    public void testToString()
    {
        BasicUGraph g = new BasicUGraph(100, 500);
        System.out.println("empty:\n" + g);
        g.genNodes(100);
        System.out.println("100 nodes:\n" + g);

        for (int i=1; i<100; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
        }
        System.out.println("100 nodes:\n" + g);
    }

    public void testGrow()
    {
        BasicUGraph g = new BasicUGraph();
        g.genNodes(16385);
        System.out.println("grow: generated nodes.");
        for (int i=1; i<16385; ++i)
            g.genEdge(g.node(i-1), g.node(i));
        System.out.println("grow: generated edges.");
    }

    public void testIterEdges()
    {
        BasicUGraph g = new BasicUGraph();
        g.genNodes(100);
        for (int i=1; i<99; ++i)
            g.genEdge(g.node(i-1), g.node(i+1));
        int i=0;
        for (Edge e : g.edges()) {
            ++i;
        }
        assertTrue(i == 98);
    }

    public void testMoveEdge()
    {
        int nSize = 10;
        BasicUGraph g = new BasicUGraph();
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i) {
            BasicUNode u = g.node(i-1);
            BasicUNode v = g.node(i);
            BasicUEdge e = g.genEdge(u, v);
        }
        CList<Edge> le = new CList<Edge>();
        for (Edge e : g.edges()) {
            System.out.println("an edge: " + e);
            le.add(e);
        }
        for (Edge e : le) {
            BasicUNode n = (BasicUNode) e.target();
            if (n != g.node(0))
                g.moveEdge(e, g.node(0), e.target());
        }
        int k=0;
        for (Edge e : g.edges()) {
            System.out.println("e: " + e);
            assertTrue(e.target().nodeId() - e.source().nodeId() == e.target().nodeId());
            ++k;
        }
        assertTrue(k == 9);
    }

    public void testConcurrentMod()
    {
        int nSize = 10;
        BasicUGraph g = new BasicUGraph();
        g.genNodes(nSize);
        try {
            for (Node n : g.nodes()) {
                // ok to alter edges.
                g.genEdge(g.node(5), g.node(4));
            }
        } catch (ConcurrentModificationException e) {
            assertTrue(false);
        }
        try {
            for (Node n : g.nodes()) {
                System.out.println("removing " + n);
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

    public void testIterOut()
    {
        int nSize = 10;
        BasicUGraph g = new BasicUGraph();
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i) 
            g.genEdge(g.node(i-1), g.node(i));
        for (Node n : g.nodes()) {
            BasicUNode bn = (BasicUNode) n;
            System.out.println(bn);
            for (Edge e = bn.out(); e != null; e = e.next()) {
                assertTrue(e.source() == n);
                System.out.println("\t(out) " + e);
            }
        }
    }

    public void testRemoveEdge()
    {
        int nSize = 10;
        BasicUGraph g = new BasicUGraph();
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i)
            g.genEdge(g.node(i-1), g.node(i));
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
            assertTrue(e.source().nodeId() == e.target().nodeId() - 1);
            assertTrue(e.source().nodeId() >= 0);
        }
    }


    public void testRemoveNode()
    {
        int nSize = 128;
        BasicUGraph g = new BasicUGraph(2,1024);
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
        }
        for (int i=2; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-2));
        }

        CList<Node> ln = new CList<Node>();
        for (Node n : g.nodes()) {
            BasicUNode bn = (BasicUNode) n;
            ln.add(n);
        }
        int eSize = g.edgeSize();
        System.out.println("edge size: " + eSize);
        assertTrue(eSize == 2 * nSize - 3);
        for (Node n : ln) {
            System.out.println("removing " + n);
            BasicUNode bn = (BasicUNode) n;
            g.remove(n);
            assertTrue(g.nodeSize() == nSize - 1);
            nSize -= 1;
            assertTrue(n.nodeId() == -1);
            assertTrue(n.out() == null);
            assertTrue(bn.degree() == 0);
            for (Edge e = bn.out(); e != null; e = e.next()) {
                System.out.println("Still in list: " + e);
                assertTrue(false);
            }
        }
        // no more nodes.
        for (Node n : g.nodes())
            assertTrue(false);
        assertTrue(g.nodeSize() == 0);
        assertTrue(g.edgeSize() == 0);
    }

    public void testShrinkEdges()
    {
        int nSize = 10;
        BasicUGraph g = new BasicUGraph(10,1);
        g.genNodes(nSize);
        for (int i=1; i<nSize; ++i) {
            g.genEdge(g.node(i), g.node(i-1));
            if (i > 1) {
                g.genEdge(g.node(i-2), g.node(i));
            }
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
    }

    /*
      The following test requires that the 
      call to _maybeShrinkEdges in BasicUGraph's
      remove(Node) method be commented out.

      I don't know how to test this otherwise..
    */
    /*
    public void testGrowAndShrinkNodeAttrs()
    {
        int nSize = 16;
        BasicUGraph g = new BasicUGraph(1,1024);
        g.genNodes(nSize);
        
        HashMap<Edge,Integer> nMap = new HashMap<Edge,Integer>();
        NodeMap<Integer> mgdNodeMap = g.createNodeMap("fred", new Integer(0));
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
        BasicUGraphTest t = new BasicUGraphTest();
        System.out.println("create:");
        t.testCreate();
        System.out.println("toString:");
        t.testToString();
        System.out.println("grow:");
        t.testGrow();
        System.out.println("move edge:");
        t.testMoveEdge();
        System.out.println("concurrent mod:");
        t.testConcurrentMod();
        System.out.println("remove edge");
        t.testRemoveEdge();
        System.out.println("iter out");
        t.testIterOut();
        System.out.println("remove node");
        t.testRemoveNode();
        System.out.println("shrink edges");
        t.testShrinkEdges();

        //t.testGrowAndShrinkEdgeAttrs();
        // see comment above the method. t.testGrowAndShrinkNodeAttrs();
    }
}
