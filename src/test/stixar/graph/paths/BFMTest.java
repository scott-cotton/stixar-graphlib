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

import stixar.util.ListCell;
import java.util.Random;
import java.util.BitSet;
import junit.framework.TestCase;

public class BFMTest extends TestCase
{
    protected Digraph digraph;
    protected Random rnd;
    protected Node source;
    protected static int numEdges=10000;
    protected static int numNodes=500;
    protected long seed;

    public BFMTest()
    {
        super("BFMTest");
        BasicDGFactory f = new BasicDGFactory();
        seed = System.currentTimeMillis();
        this.rnd = new Random(seed);
        f.genNodes(numNodes);
        f.genEdges(numEdges);
        digraph = f.digraph();
        source = digraph.node(10);
        this.digraph = f.digraph();
    }

    public void testNative()
    {
        System.out.println("seed: " + seed);
        int[] weights = new int[numEdges];
        for (int i=0; i<numEdges; ++i) {
            weights[i] = rnd.nextInt(500);
            if (i % 100 == 0)
                weights[i] -= 180;
        }
        BFMNative bfm = new BFMNative(digraph, 
                                      source, 
                                      new IntEdgeMap(weights), 
                                      new IntNodeMap(new int[digraph.nodeAttrSize()]), 
                                      null);
        bfm.run();
        reportCycle(bfm, weights);
    }

    protected void reportCycle(BFMNative bfm, int[] weights)
    {
        Path p=null;
        if ((p = bfm.negCycle()) != null) {
            System.out.println("neg cycle: ");
            ListCell<Edge> cell = p.edges().firstCell();
            while(cell != null) {
                Edge e = cell.value();
                Node s = e.source();
                System.out.printf("(n%d) %d ", s.nodeId(), e.getInt(weights));
                cell = cell.next();
                if (cell == null) {
                    System.out.printf("(n%d)\n", e.target().nodeId());
                }
            }
        } else {
            System.out.println("no neg cycle");
        }
    }


    public static void main(String[] args)
    {
        BFMTest t = new BFMTest();
        t.testNative();
    }
}
