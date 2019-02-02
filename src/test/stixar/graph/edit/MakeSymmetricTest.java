package stixar.graph.edit;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.attr.EdgeMap;


import stixar.util.Pair;

import junit.framework.TestCase;

public class MakeSymmetricTest extends TestCase
{
    int nodeSize = 500;
    int edgeSize = 5000;
    public MakeSymmetricTest()
    {
        super("MakeSymmetric");
    }

    public void testMS()
    {
        BasicDGFactory fact = new BasicDGFactory(nodeSize, edgeSize);
        fact.genNodes(nodeSize);
        fact.genEdges(edgeSize);
        Pair<BasicNode,BasicNode> p = fact.genSourceAndSink();
        BasicDigraph dg = fact.digraph();
        MakeSymmetric sym = new MakeSymmetric();
        sym.edit(dg);
        EdgeMap<Edge> flip = sym.revAttrs();
        for (Edge e : dg.edges()) {
            assertTrue(e.get(flip) != null);
        }
    }
}
