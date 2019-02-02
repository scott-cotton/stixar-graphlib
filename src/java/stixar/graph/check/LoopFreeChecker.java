package stixar.graph.check;

import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.DFS;


/**
   Check a graph for loops.

   @see DigraphProperty#LoopFree
 */
public class LoopFreeChecker 
    implements DigraphChecker
{
    protected Edge loop;

    public LoopFreeChecker()
    {
        loop = null;
    }

    /**
       Return a found loop from the last checked digraph.
     */
    public Edge loop()
    {
        return loop;
    }

    public boolean check(Digraph dg)
    {
        loop = null;
        for (Edge e : dg.edges()) {
            if (e.source() == e.target()) {
                loop = e;
                break;
            }
        }
        return loop == null;
    }

}
