package stixar.graph.edit;

import stixar.graph.MutableDigraph;
import stixar.graph.Edge;
import stixar.graph.Node;
import stixar.graph.attr.EdgeMap;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import stixar.util.Pair;

/**
   Make a graph symmetric.
 */
public class MakeSymmetric
{
    /**
       Interface for defining what to do with self loops.
     */
    public interface SelfLoopHandler
    {
        public void handle(Edge e);
    }

    /**
       Default Self loop handler
     */
    public class DefaultSelfLoopHandler implements SelfLoopHandler
    {
        /**
           @throws IllegalArgumentException
         */
        public void handle(Edge e)
        {
            throw new IllegalArgumentException();
        }
    }
    protected HashMap<Pair<Node,Node>, Edge> edgeMap;
    protected EdgeMap<Edge> revAttrs;
    protected MutableDigraph digraph;
    protected SelfLoopHandler selfLoopHandler;

    /**
       Create a new symmetrizer.
     */
    public MakeSymmetric()
    {
        edgeMap = new HashMap<Pair<Node,Node>, Edge>();
        revAttrs = null;
        digraph = null;
        this.selfLoopHandler = new DefaultSelfLoopHandler();
    }

    /**
       Produce an edge attribute array describing the reverse of every edge
       in the digraph from the last run.
     */
    @SuppressWarnings("unchecked")
    public EdgeMap<Edge> revAttrs()
    {
        if (digraph == null) {
            throw new IllegalStateException
                ("MakeSymmetric revAttrs called prior to edit()");
        }
        if (revAttrs != null) 
            return revAttrs;
        revAttrs = digraph.createEdgeMap((Edge) null);
        for (Map.Entry<Pair<Node,Node>, Edge> entry : edgeMap.entrySet()) {
            Edge e = entry.getValue();
            if (e.target().equals(e.source())) {
                selfLoopHandler.handle(e);
                continue;
            }
            Pair<Node,Node> revKey = new Pair<Node,Node>(e.target(), e.source());
            Edge rev = edgeMap.get(revKey);
            assert rev != null;
            e.set(revAttrs, rev);
        }
        return revAttrs;
    }

    /**
       Retrieve the self loop handler.
     */
    public SelfLoopHandler selfLoopHandler()
    {
        return selfLoopHandler;
    }

    /**
       Set and retrieve the self loop handler.
     */
    public SelfLoopHandler SelfLoopHandler(SelfLoopHandler h)
    {
        return selfLoopHandler = h;
    }

    /**
       Make a mutable graph symmetric by adding some edges.

       @param mdg the mutable digraph to be made symmetric.
       @throws IllegalArgumentException on a self loop 
       if the {@link #selfLoopHandler} throws IllegalArgumentException
       on a self loop.  This is the default behavior.
     */
    public void edit(MutableDigraph mdg)
    {
        digraph = mdg;
        edgeMap = new HashMap<Pair<Node,Node>, Edge>(mdg.edgeSize() * 2);
        for (Edge e : mdg.edges()) {
            Pair<Node,Node> key = new Pair<Node,Node>(e.source(), e.target());
            edgeMap.put(key, e);
        }
        ArrayList<Edge> newEdges = new ArrayList<Edge>();
        for (Edge e : edgeMap.values()) {
            Pair<Node,Node> key = new Pair<Node,Node>(e.target(), e.source());
            Edge rev;
            if ((rev = edgeMap.get(key)) == null) {
                rev = mdg.genEdge(e.target(), e.source());
                newEdges.add(rev);
            }
        }
        for (Edge e : newEdges) {
            Pair<Node,Node> key = new Pair<Node,Node>(e.source(), e.target());
            edgeMap.put(key, e);            
        }
    }
}
