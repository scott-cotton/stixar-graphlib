package stixar.graph.check;

import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
   Checks if a graph is symmetric.

   @see DigraphProperty#LoopFree
 */
public class SymmetricChecker
    implements DigraphChecker
{
    protected Edge witness;
    protected HashMap<Pair<Node,Node>, Edge> edgeMap;

    public SymmetricChecker()
    {
        witness = null;
        edgeMap = new HashMap<Pair<Node,Node>, Edge>();
    }

    /**
       Return an edge without a reverse in the graph if such
       an edge exists from the last {@link #check}.
     */
    public Edge witness()
    {
        return witness;
    }

    public boolean check(Digraph dg)
    {
        witness = null;
        edgeMap.clear();
        for (Edge e : dg.edges()) {
            Node s = e.source();
            Node t = e.target();
            edgeMap.put(new Pair<Node,Node>(s,t), e);
        }
        for (Map.Entry<Pair<Node,Node>, Edge> e : edgeMap.entrySet()) {
            Pair<Node,Node> key = e.getKey();
            Pair<Node,Node> revKey = new Pair<Node,Node>(key.second, key.first);
            if (!checkEdge(revKey)) {
                witness = e.getValue();
                return false;
            }
        }
        return true;
    }

    protected boolean checkEdge(Pair<Node,Node> key)
    {
        return edgeMap.containsKey(key);
    }
}
