package stixar.graph.check;

import stixar.graph.UGraph;
import stixar.graph.Node;
import stixar.graph.Edge;

/**
   Checks whether or not an undirected graph is a tree.

   @see UGraphProperty#Tree
 */
public class UTreeChecker extends UForestChecker
    implements UGraphChecker
{
    public UTreeChecker()
    {
        super();
    }

    public boolean check(UGraph g)
    {
        if (!super.check(g))
            return false;
        int ttlRoots = 0;
        for (Node n : g.nodes()) {
            if (n.get(parents) == null)
                ttlRoots++;
            if (ttlRoots > 1)
                return false;
        }
        return true;
    }
}
