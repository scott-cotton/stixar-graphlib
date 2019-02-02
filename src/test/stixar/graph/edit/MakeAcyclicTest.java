package stixar.graph.edit;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.check.AcyclicChecker;

import junit.framework.TestCase;

public class MakeAcyclicTest extends TestCase
{
    public MakeAcyclicTest()
    {
        super("MakeAcyclicTest");
    }

    public void testIt()
    {
        BasicDGFactory f = new BasicDGFactory(100, 1000);
        f.genNodes(100);
        f.genEdges(10000, true, false);
        BasicDigraph digraph = f.digraph();
        AcyclicChecker ck = new AcyclicChecker();
        if (!ck.check(digraph)) {
            MakeAcyclic mac = new MakeAcyclic();
            mac.edit(digraph);
            assertTrue(ck.check(digraph));
        }
    }
}
