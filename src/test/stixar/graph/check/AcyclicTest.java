package stixar.graph.check;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;

import junit.framework.TestCase;

import java.util.Random;

public class AcyclicTest extends TestCase
{
    static final int nodeSize = 100;

    public AcyclicTest() { super(); }

    public void testAcyclic()
    {
        BasicDGFactory factory = new BasicDGFactory();
        BasicNode[] nodes = new BasicNode[nodeSize];
        for (int i=0; i<nodeSize; ++i) {
            nodes[i] = factory.node();
        }
        for (int i=0; i<nodeSize-1; ++i) {
            factory.edge(nodes[i], nodes[i+1]);
        }
        factory.edge(nodes[nodeSize-1], nodes[0]);
        Digraph digraph = factory.digraph();
        AcyclicChecker ck = new AcyclicChecker();
        assertFalse(ck.check(digraph));
    }
}
