package stixar.graph.flow;

import stixar.graph.edit.MakeSymmetric;
import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.Edge;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.IntEdgeMap;

import stixar.util.Pair;

import java.util.Random;

import junit.framework.TestCase;


public class MaxFlowTest extends TestCase
{
    static final int nodeSize = 5000 * 1;
    static final int edgeSize = 15000 * 1;

    protected Random rnd;

    public MaxFlowTest() 
    { 
        super("MaxFlow"); 
        rnd = new Random();
    }

    public void testPR()
    {
        BasicDGFactory fact = new BasicDGFactory(nodeSize, edgeSize);
        fact.genNodes(nodeSize);
        fact.genEdges(edgeSize);
        Pair<BasicNode,BasicNode> p = fact.genSourceAndSink();
        BasicDigraph dg = fact.digraph();
        MakeSymmetric sym = new MakeSymmetric();
        sym.edit(dg);
        EdgeMap<Edge> flip = sym.revAttrs();
        IntEdgeMap caps = dg.createIntEdgeMap();
        for (Edge e : dg.edges()) {
            Edge rev = e.get(flip);
            if (rev.getInt(caps) != 0) {
                if (rnd.nextBoolean()) {
                    e.setInt(caps, 0);
                } else {
                    e.setInt(caps, rnd.nextInt(100));
                    rev.setInt(caps, 0);
                }
            } else {
                e.setInt(caps, rnd.nextInt(100));
            }
        }
        MaxFlow pr = new MaxFlow(dg, p.first, p.second, caps, flip);
        pr.run();
        pr.check();
        System.out.println(pr.statistics());
    }
                                               
    

    public static void main(String[] args)
    {
        MaxFlowTest t = new MaxFlowTest();
        t.testPR();
    }
}
