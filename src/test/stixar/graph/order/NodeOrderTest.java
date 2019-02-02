package stixar.graph.order;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.Node;

import stixar.graph.order.NodeOrder;

import junit.framework.TestCase;


public class NodeOrderTest extends TestCase
{
    static final int nodeSize = 1000;
    static final int edgeSize = 4000;

    public NodeOrderTest() { super(); }

    public void testReverse()
    {
        BasicDGFactory factory = new BasicDGFactory();
        factory.genNodes(nodeSize);
        factory.genEdges(edgeSize);
        Digraph dg = factory.digraph();
        int[] perm = new int[nodeSize];
        int top = nodeSize - 1;
        for (int i=0; i<nodeSize; ++i) {
            perm[i] = top - i;
        }
        NodeOrder no = new NodeOrder(dg, perm);
        int i = 0;
        for (Node n : dg.nodes(no)) {
            assertTrue(n.nodeId() == top - i);
            ++i;
        }
        no.reverse();
        int j = 0;
        for (Node n : dg.nodes(no)) {
            assertTrue(n.nodeId() == j);
            ++j;
        }
    }
}
