package stixar.graph.check;

import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import java.util.Arrays;

/**
   Check whether or not a digraph is a forest.

   @see DigraphProperty#Forest
 */
// XXX filter?
public class ForestChecker
    implements DigraphChecker
{
    protected Edge[] parents;
    protected Digraph digraph;
    protected boolean isTree;
    
    public ForestChecker()
    {
        parents = new Edge[512];
        digraph = null;
        isTree = true;
    }

    public boolean check(Digraph dg)
    {
        if (dg.edgeSize() >= dg.nodeSize())
            return false;
        int nsz = dg.nodeSize();
        if (parents.length < nsz)
            parents = new Edge[nsz];
        digraph = dg;
        Arrays.fill(parents, null);
        isTree = true;
        for (Edge e : dg.edges()) {
            Node t = e.target();
            if (t.get(parents) != null) {
                isTree = false;
                break;
            } else {
                t.set(parents, e);
            }
        }
        return isTree;
    }
}
