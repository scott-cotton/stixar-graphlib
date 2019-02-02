package stixar.graph.conn;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.Node;
import stixar.graph.attr.NodeMatrix;
import stixar.graph.attr.ByteNodeMatrix;
import java.util.Random;

import junit.framework.TestCase;

public class TransitivityTest extends TestCase
{
    
    public TransitivityTest() { super(); }

    // build 2 cycles, link one to the next, 
    // test all combinations of reachability.
    public void testTC()
    {
        BasicNode[] nodes = new BasicNode[20];
        BasicDGFactory factory = new BasicDGFactory();
        for (int i=0; i<10; ++i) {
            nodes[i] = factory.node();
        }
        for (int i=0; i<9; ++i) {
            factory.edge(nodes[i], nodes[i+1]);
        }
        factory.edge(nodes[9], nodes[0]);

        for (int i=10; i<20; ++i) {
            nodes[i] = factory.node();
        }
        for (int i=10; i<19; ++i) {
            factory.edge(nodes[i], nodes[i+1]);
        }
        factory.edge(nodes[19], nodes[10]);
        factory.edge(nodes[9], nodes[10]);


        Digraph digraph = factory.digraph();
        NodeMatrix<Boolean> reachMat = Transitivity.compactClosure(digraph);

        for (int i=0; i<10; ++i) {
            for (int j=0; j<10; ++j) {
                assertTrue(reachMat.get(nodes[i], nodes[j]));
            }
        }

        for (int i=10; i<20; ++i) {
            for (int j=10; j<20; ++j) {
                assertTrue(reachMat.get(nodes[i], nodes[j]));
            }
        }


        for (int i=0; i<10; ++i) {
            for (int j=10; j<20; ++j) {
                assertTrue(reachMat.get(nodes[i], nodes[j]));
            }
        }


        for (int i=10; i<20; ++i) {
            for (int j=0; j<10; ++j) {
                assertFalse(reachMat.get(nodes[i], nodes[j]));
            }
        }
    }

    public void testTCRnd()
    {
        int nn = 10000;
        int ne = 80000;
        BasicDGFactory factory = new BasicDGFactory();
        factory.genNodes(nn);
        factory.genEdges(ne);
        Digraph digraph = factory.digraph();
        NodeMatrix<Boolean> reachMat = Transitivity.compactClosure(digraph);
    }

    public static void main(String[] args)
    {
        TransitivityTest t = new TransitivityTest();
        t.testTCRnd();
    }
}
