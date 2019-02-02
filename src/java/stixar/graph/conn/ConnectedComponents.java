package stixar.graph.conn;

import stixar.graph.Graph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.DFS;

/**
   Compute the connected components for an arbitrary graph.
   <p>
   Two vertices <tt>u,v</tt> belong to the same connected component
   exactly if there is exists some path between them, that is 
   either if there is a path from <tt>u</tt> to <tt>v</tt>
   or a path from <tt>v</tt> to <tt>u</tt>.
   </p>
   <p>
   This algorithm works for either directed or undirected graphs
   and is based on a simple contagious examination of the edges
   in the graph.
   </p>
 */
public class ConnectedComponents
    extends DFS.Visitor
{
    protected Node[] reps;
    protected DFS dfs;
    protected Graph graph;

    public ConnectedComponents(Graph g)
    {
        reps = new Node[g.nodeAttrSize()];
        graph = g;
        dfs = new DFS(g, this);
    }

    public void run()
    {
        dfs.run();
    }

    public void root(Node n)
    {
        n.set(reps, n);
    }

    public Node[] components()
    {
        return reps;
    }

    public void startEdge(Edge e)
    {
        e.target().set(reps, e.source().get(reps));
    }
}