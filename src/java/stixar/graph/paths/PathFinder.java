package stixar.graph.paths;

import stixar.graph.Graph;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.DFS;

import java.util.Arrays;

/**
   A simple reachability class for computing a small number 
   of reachability queries.
   <p>
   If many reachability computations are required, and the
   {@link Path} of reachability is not needed for the 
   queries, one should consider using {@link stixar.graph.conn.Transitivity}
   as it will be more efficient.
   </p>
 */
public class PathFinder extends DFS.Visitor implements Filtering
{
    protected Graph graph;
    protected DFS dfs;
    protected Edge[] parents;
    protected Node source;
    protected Node target;
    protected boolean found;


    /**
       Construct a new reachability finder for a graph.
       @param graph the graph in which to search for reachability.
     */
    public PathFinder(Graph graph)
    {
        this.graph = graph;
        this.dfs = new DFS(graph, new Visitor());
        this.parents = new Edge[graph.nodeAttrSize()];
    }

    /**
       Return the current source node.
     */
    public Node source()
    {
        return source;
    }

    /**
       Return the current target node.
     */
    public Node target()
    {
        return target;
    }

    /**
       Set the current source node.
     */
    public Node source(Node n)
    {
        return source = n;
    }

    /**
       Set the current target node.
     */
    public Node target(Node n)
    {
        return target = n;
    }

    /**
       Compute reachability from one node to another.

       @param s the node from which to search for <tt>t</tt>.
       @param t the node for which a search is performed from <tt>s</tt>.
       @return whether <tt>s</tt> can reach <tt>t</tt>
     */
    public boolean reaches(Node s, Node t)
    {
        this.source = s;
        this.target = t;
        dfs.run();
        return found;
    }

    /**
       Compute a path from <tt>s</tt> to <tt>t</tt>.

       @param s the node from which to search for <tt>t</tt>.
       @param t the node for which a search is performed from <tt>s</tt>.
       @return a Path from <tt>s</tt> to <tt>t</tt> if such a path
       exists, otherwise return <tt>null</tt>. If in addition <tt>s != t</tt>,
       the path is guaranteed to be simple.
     */
    public Path path(Node s, Node t)
    {
        if (reaches(s, t)) {
            Path p = new Path();
            Node n = t;
            Edge e;
            do {
                e = n.get(parents);
                p.prepend(e);
                n = e.source();
            } while(n != s);
            return p;
        }
        return null;
    }

    protected void reset()
    {
        dfs.reset();
        Arrays.fill(parents, null);
        found = false;
    }

    protected void run()
    {
        if (source == null || target == null)
            throw new IllegalStateException
                ("Source or target not specified in Reach.");
        reset();
        dfs.run();
    }


    protected class Visitor extends DFS.Visitor
    {
        /**
           Internal method which records a tree.
        */
        public final void treeEdge(Edge e)
        {
            e.target().set(parents, e);
        }
        
        /**
           Internal method.
        */
        public final void discover(Node n)
        {
            if (n.equals(target))
                found = true;
        }

        /**
           Internal method.
        */
        public final boolean done()
        {
            return !found;
        }
    }
}
