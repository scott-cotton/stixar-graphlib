package stixar.graph.check;

import stixar.util.Pair;
import stixar.graph.Node;

/**
   Check whether a digraph has no edges <tt>(u,v)</tt> such that
   there exists an edge <tt>(v,u)</tt>.
 */
public class AntiSymmetricChecker extends SymmetricChecker
    implements DigraphChecker
{
    public AntiSymmetricChecker()
    {
        super();
    }

    protected  boolean checkEdge(Pair<Node,Node> p)
    {
        return !edgeMap.containsKey(p);
    }
}
