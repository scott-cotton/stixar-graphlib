package stixar.graph.check;

import stixar.graph.Edge;
import stixar.graph.Node;
import stixar.graph.Filtering;
import stixar.graph.Digraph;
import stixar.graph.search.DFS;


import java.util.LinkedList;

/**
   Checks whether or not a digraph is acyclic.
 */
public class AcyclicChecker extends DFS.Visitor
    implements DigraphChecker, Filtering                                    
{
    LinkedList<Edge> backEdges;

    /**
       Construct a new Acyclic visitor instance.
     */
    public AcyclicChecker()
    {
        backEdges = new LinkedList<Edge>();
    }

    public boolean check(Digraph digraph)
    {
        backEdges.clear();
        DFS dfs = new DFS(digraph, this);
        dfs.run();
        return backEdges.isEmpty();
    }

    /**
       Method which is called when there is a back edge (cyclic)
       in the DFS search.
     */
    public void backEdge(Edge e)
    {
        backEdges.add(e);
        //e.source().remove(e);
    }

    /**
       Stops the algorithm after the first cycle is found.
     */
    public boolean done()
    {
        return !backEdges.isEmpty();
    }

    /**
       List of all edges which make the graph cyclic in this
       DFS traversal.
     */
    public LinkedList<Edge> backEdges() 
    { 
        return backEdges; 
    }
}
