package stixar.graph.search;

import stixar.graph.gen.BasicDGFactory;
import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.Node;
import stixar.graph.Edge;



import java.util.Random;
import junit.framework.TestCase;

public class DFSTest extends TestCase
{
    static int numNodes = 6000;
    static int numEdges = 15000;
    protected Digraph digraph;

    public DFSTest() 
    { 
        super();
        BasicDGFactory factory = new BasicDGFactory();
        factory.genNodes(numNodes);
        factory.genEdges(numEdges);
        this.digraph = factory.digraph();
    }

    public void testDFSNR()
    {
        DFS dfs = new DFS(digraph, new MyDFSVisitor(), true);
        long start = System.currentTimeMillis();
        dfs.run();
        long end = System.currentTimeMillis();
        System.out.println("took " + (end - start));
    }


    public void testDFS()
    {
        DFS dfs = new DFS(digraph, new MyDFSVisitor());//, false);
        long start = System.currentTimeMillis();
        dfs.run();
        long end = System.currentTimeMillis();
        System.out.println("took " + (end - start));
    }

    public void testCompat()
    {
        MyDFSVisitor vnr = new MyDFSVisitor();
        MyDFSVisitor vr = new MyDFSVisitor();
        DFS nr = new DFS(digraph, vnr, true);
        DFS r = new DFS(digraph, vr, false);
        nr.run();
        r.run();
        assertTrue(vnr.toString().equals(vr.toString()));
    }

    public static void main(String[] args)
    {
        DFSTest t = new DFSTest();
        System.out.println("recursive");
        t.testDFS();
        System.out.println("non recursive");
        t.testDFSNR();
        System.out.print("compat? ");
        t.testCompat();
        System.out.println("yes");
    }
}

class MyDFSVisitor extends DFS.Visitor
{
    protected StringBuffer sb;
    public MyDFSVisitor() 
    {
        sb = new StringBuffer();
    }

    public String toString()
    {
        return sb.toString();
    }

    public void discover(Node n) 
    {
        sb.append("d" + n.nodeId());
        //System.out.println("discover node " + n.nodeId());
    }

    public void finish(Node n)
    {
        sb.append("f" + n.nodeId());
        //System.out.println("finish node " + n.nodeId());
    }

    public void treeEdge(Edge e)
    {
        //sb.append("t" + e.edgeId());
        //System.out.println("tree edge: " + e.source().nodeId() + ", " + e.target().nodeId());
    }

    public void backEdge(Edge e)
    {
        //sb.append("b" + e.edgeId());
        //System.out.println("back edge: " + e.source().nodeId() + ", " + e.target().nodeId());
    }
}
