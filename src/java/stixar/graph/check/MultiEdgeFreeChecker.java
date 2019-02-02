package stixar.graph.check;

import stixar.graph.Graph;
import stixar.graph.Digraph;
import stixar.graph.UGraph;
import stixar.graph.Node;
import stixar.graph.Edge;

/**
   Check whether or not a graph has multiple edges.
 */
public class MultiEdgeFreeChecker 
    implements  DigraphChecker, UGraphChecker
{
    protected boolean[] nodeMarks;

    public MultiEdgeFreeChecker()
    {
        nodeMarks = new boolean[1024];
        
    }

    public boolean check(Digraph g)
    {
        return check((Graph) g);
    }

    public boolean check(UGraph g)
    {
        return check((Graph) g);
    }


    public boolean check(Graph g)
    {
        if (nodeMarks.length < g.nodeAttrSize()) {
            nodeMarks = new boolean[Math.max(g.nodeAttrSize(), nodeMarks.length * 2)];
        }
        for (Node n : g.nodes()) {
            for (Edge e = n.out(); e != null; e = e.next()) {
                Node t = e.target();
                t.setBool(nodeMarks, false);
            }
            for (Edge e = n.out(); e != null; e = e.next()) {
                Node t = e.target();
                if (t.getBool(nodeMarks)) {
                    return false;
                }
                t.setBool(nodeMarks, true);
            }
        }
        return true;
    }
}
