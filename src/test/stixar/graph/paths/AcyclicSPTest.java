package stixar.graph.paths;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.edit.MakeAcyclic;
import stixar.graph.Node;
import stixar.graph.Edge;


import java.util.Random;
import junit.framework.TestCase;

public class AcyclicSPTest extends TestCase
{
    protected BasicDigraph digraph;
    protected Random rnd;
    protected static int nodeSize=100;
    protected static int edgeSize=8000;
    
    public AcyclicSPTest()
    {
        super("TestCase");
        initDigraph();
    }

    public void initDigraph()
    {
        /*
          The following is a good argument to make graph
          generation and editing easier...
         */
        rnd = new Random();
        BasicDGFactory fact = new BasicDGFactory();
        fact.genNodes(nodeSize);
        fact.genEdges(edgeSize, false /* no mult */, false /* no self */);
        digraph = fact.digraph();
        MakeAcyclic ac = new MakeAcyclic();
        ac.edit(digraph);
    }


    public void testNative()
    {
        /*
        Node source = digraph.node(rnd.nextInt(nodeSize));
        int[] weights = new int[digraph.edgeAttrSize()];
        for (int i=0; i<weights.length; ++i) {
            weights[i] = rnd.nextInt(1000) - 50;
        }
        AcyclicSP.I spi = new AcyclicSP.I(digraph, source, weights);
        spi.run();
        int[] dists = spi.distances();
        Edge[] parents = spi.parents();
        for (Edge e: parents) {
            if (e == null) continue;
            System.out.printf("%d[%d] %d[%d] w=%d\n",
                              e.source().nodeId(), dists[e.source().nodeId()],
                              e.target().nodeId(), dists[e.target().nodeId()],
                              weights[e.edgeId()]);
        }
        */
    }

    public static void main(String[] args)
    {
        AcyclicSPTest t = new AcyclicSPTest();
        t.testNative();
    }
}
