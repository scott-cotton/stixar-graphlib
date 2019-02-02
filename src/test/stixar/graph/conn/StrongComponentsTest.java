package stixar.graph.conn;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;

import junit.framework.TestCase;

public class StrongComponentsTest extends TestCase
{
    
    public StrongComponentsTest() { super(); }

    // build 2 cycles, make sure all element in each have same 
    // component and that the two components aren't equal.
    public void testSCCCycle()
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
        StrongComponents scc = new StrongComponents(digraph);

        scc.run();
        int c1 = scc.component(nodes[0]);
        for (int i=1; i<10; ++i) {
            System.out.println(nodes[i] + ": " + scc.component(nodes[i]));
            assertEquals(new Integer(scc.component(nodes[i])), new Integer(c1));
        }

        int c2 = scc.component(nodes[10]);
        for (int i=11; i<20; ++i) {
            System.out.println(nodes[i] + ": " + scc.component(nodes[i]));
            assertEquals(new Integer(scc.component(nodes[i])), new Integer(c2));
        }
        System.out.println(digraph);
        assertTrue(new Boolean(c1 != c2));
    }
    
}
