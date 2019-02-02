package stixar.graph.edit;

import stixar.graph.search.DFS;

import stixar.graph.Edge;
import stixar.graph.Node;
import stixar.graph.MutableGraph;

import stixar.util.CList;
import stixar.util.BinaryPQ;

/**
   Make a graph acyclic.
 */
public class MakeAcyclic
{
    protected CList<Edge> backEdges;

    /**
       Create a new acyclic maker.
     */
    public MakeAcyclic()
    {
        backEdges = new CList<Edge>();
    }

    /**
       Make a mutable graph acyclic by removing some edges.

       @param mdg the mutable digraph to be made acyclic.
     */
    public void edit(MutableGraph mdg)
    {
        backEdges.clear();
        DFS dfs = new DFS(mdg, new Visitor());
        dfs.run();
        for (Edge me : backEdges) {
            mdg.remove(me);
        }
    }


    /*
      Hide visitor methods from javadocs..
     */
    private class Visitor extends DFS.Visitor 
    {
        public void backEdge(Edge e)
        {
            backEdges.add(e);
        }
    }

    /**
       Produce a list of the removed edges.
       @return a list of the edges removed in making the graph 
       acyclic.
     */
    public CList<Edge> removedEdges()
    {
        return backEdges;
    }
}
