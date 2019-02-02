package stixar.graph.edit;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;


import junit.framework.TestCase;


public class TransposerTest extends TestCase
{
    public TransposerTest()
    {
        super("TransposerTest");
    }

    public void testTranspose()
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
        BasicDigraph dg = factory.digraph();
        System.out.println(dg);
        Transposer tp = new Transposer();
        tp.edit(dg);
        System.out.println(dg);
    }

    public static void main(String[] args)
    {
        TransposerTest tst = new TransposerTest();
        tst.testTranspose();
    }
}
