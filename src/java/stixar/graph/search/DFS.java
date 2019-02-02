package stixar.graph.search;

import stixar.graph.Graph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.GraphFilter;
import stixar.graph.attr.NodeMap;

import stixar.graph.order.NodeOrder;

import java.util.Arrays;

/**
   Filtering depth first search algorithm.

   Depth first search makes use of a visitor object whose
   methods are invoked throughout the search.   The visitor
   has methods indicating the various contexts which can occur
   during the search and can be used to construct many different
   algorithms.
 */
public class DFS implements Algorithm, Filtering
{
    /**
       Different statuses of nodes during a depth first search.
     */
    public enum Color 
    { 
        /** not yet seen */ white, 
        /** processing, in dfs stack */ grey, 
        /** already processed */ black ;
    }

    /**
       Various DFS status information kept for every node.
       A managed NodeMap&lt;Status&gt is availabel by the
       key returned from the method key {@link #key}
     */
    public static final class Status
    {
        
        public Status()
        {
            color = Color.white;
            startNum = -1;
            finishNum = -1;
        }

        public Color color;
        public int startNum;
        public int finishNum;
    }

    protected Graph graph;
    protected NodeMap<Status> statMap;
    protected NREntry[] nrStack;
    protected Visitor visitor;
    protected int dfsStart;
    protected int dfsFinish;
    protected int lastDfsStart;
    protected GraphFilter filt;
    protected boolean recursive;
    protected Object key;

    /**
       Construct a DFS algorithm from a graph and a DFS visitor.

       The default is to use the nonrecursive implementation.
     */
    public DFS(Graph graph, Visitor vis)
    {
        this(graph, vis, false);
    }

    /**
       Construct a DFS algorithm from a graph and a DFS visitor,
       specifying whether or not to use the non recursive 
       implementation.
       <p>
       If the non recursive implementation is used, then an explicit
       stack will be created rather than using the implicit call
       stack in the recursive style.  As the JVM has a limit on 
       the depth call stack, this can limit the size of the 
       graphs that can be handled.  On the other hand, the non recursive
       version requires additional explicit storage (an array 
       of size graph.nodeSize()).  The default is to use the non 
       recursive version.  
       </p>
       <p>
       Experiments comparing the speeds of the two implementations were 
       inconclusive because there was too much variance.  Sometimes
       either implementation was faster, depending on the calling
       context.
       </p>

       @param graph a graph on which to perform DFS
       @param vis a visitor 
       @param recursive whether or not to use an explicit stack.
     */
    public DFS(Graph graph, Visitor vis, boolean recursive)
    {
        this.graph = graph;
        this.filt = graph.getFilter();
        this.key = new Object();
        this.statMap = graph.createNodeMap(key);

        this.dfsStart = 0;
        this.visitor = vis;
        this.nrStack = null;
        this.recursive = recursive;
        this.lastDfsStart = -1;
        if (!recursive) {
            int sz = graph.nodeSize();
            this.nrStack = new NREntry[sz];
            for (int i=0; i<sz; ++i) {
                nrStack[i] = new NREntry();
            }
        }
        initStatMap();
    }

    /**
       Initialize the node status map.  This is done
       upon construction.
     */
    public void initStatMap()
    {
        for (Node n : graph.nodes()) {
            statMap.set(n, new Status());
        }
    }

    public void run()
    {
        Status stat = null;
        for (Node n: graph.nodes()) {
            if (filt != null && filt.filter(n)) continue;
            stat = statMap.get(n);
            if (stat.color == Color.white) {
                visitor.root(n);
                if (recursive) 
                    rVisit(n);
                else
                    nrVisit(n);
            }
        }
    }

    /**
       Return the managed attribute key for a NodeMap&lt;Status&gt;.
     */
    public Object statKey()
    {
        return key;
    }

    /**
       reinitialize everything.
     */
    public void reset()
    {
        if (nrStack != null) {
            int sz = graph.nodeSize();
            this.nrStack = new NREntry[sz];
            for (int i=0; i<sz; ++i) {
                nrStack[i] = new NREntry();
            }
        }
        initStatMap();
    }

    /**
       Return the dfs number for the node <tt>n</tt>

       @param n the node whose dfs number is to be queried.
       @return the place of <tt>n</tt> in the order in which this
       algorithm visits the nodes.
     */
    public int dfsStart(Node n)
    {
        return statMap.get(n).startNum;
    }

    
    /**
       Return the {@link #Status} object associated with a node.
       @param n the node whose status is to be found.
     */
    public Status status(Node n)
    {
        return statMap.get(n);
    }

    /**
       Construct a node order in the order in which DFS
       visited the nodes.

       @return a NodeOrder representing the dfs order.
     */
    public NodeOrder order()
    {
        int[] dfsStarts = new int[graph.nodeSize()];
        for (Node n : graph.nodes()) {
            Status nStat = statMap.get(n);
            dfsStarts[n.nodeId()] = nStat.startNum;
        }
        return new NodeOrder(graph, dfsStarts);
    }


    /**
       Visit a node and recursively those reachable from it.
       @param u the node to be visited.
     */
    public final void visit(Node u)
    {
        if (recursive) rVisit(u);
        else nrVisit(u);
    }

    /**
       Visit a node in the graph, and recursively the nodes
       reachable from it.

       @param u the node to be visited.
     */
    public final void rVisit(Node u)
    {
        if (visitor.done()) return;
        if (!visitor.follow(u)) return;
        Status uStat = statMap.get(u);
        uStat.color = Color.grey;
        uStat.startNum = dfsStart++;
        visitor.discover(u);
        for(Edge e = u.out(); e != null; e = e.next()) {
            if (filt != null && filt.filter(e)) continue;
            visitor.startEdge(e);
            Node v = e.target();
            Status vStat = statMap.get(v);
            switch(vStat.color)
                {
                case white:
                    visitor.treeEdge(e);
                    if (filt != null  && filt.filter(v)) break;
                    rVisit(v);
                    break;
                case grey:
                    visitor.backEdge(e);
                    break;
                case black:
                    if (vStat.startNum > uStat.startNum) {
                        visitor.fwdEdge(e);
                    } else {
                        visitor.crossEdge(e);
                    }
                    break;
                }
            visitor.finishEdge(e);
        }
        uStat.color = Color.black;
        uStat.finishNum = dfsFinish++;
        visitor.finish(u);
    } 

    protected static final class NREntry
    {
        Edge edge;
        Node node;
    }

    /**
       Non recursive, explicit stack, version of dfs visit.
     */
    protected void nrVisit(Node n)
    {
        int top = 0;
        NREntry entry = nrStack[top];
        entry.node = n;
        entry.edge = n.out();
        visitor.discover(n);
        Status nStat = statMap.get(n);
        nStat.color = Color.grey;
        nStat.startNum = dfsStart++;
        top++;
        while(top > 0) {
            entry = nrStack[top - 1];
            Edge e = entry.edge;
            if (e != null) {
                entry.edge = e.next();
            }
            while(e != null && !visitor.done()) {
                if (filt != null && filt.filter(e)) { 
                    e = entry.edge = e.next(); 
                    continue;
                }
                visitor.startEdge(e);
                Node v = e.target();
                Status vStat = statMap.get(v);
                switch(vStat.color) {
                case white:
                    vStat.color = Color.grey;
                    vStat.startNum = dfsStart++;
                    visitor.treeEdge(e);
                    visitor.discover(v);
                    if (visitor.follow(v)) {
                        entry.edge = e;
                        e = v.out();
                        entry = nrStack[top++];
                        entry.edge = e;
                        entry.node = v;
                        continue;
                    }
                    break;
                case grey:
                    visitor.backEdge(e);
                    break;
                case black:
                    Status sStat = statMap.get(e.source());
                    if (vStat.startNum > sStat.startNum) {
                        visitor.fwdEdge(e);
                    } else {
                        visitor.crossEdge(e);
                    }
                    break;
                default:
                    throw new Error("Illegal dfs color: " + vStat.color);
                }
                e = e.next();
            }
            Node u = entry.node;
            Status uStat = statMap.get(u);
            uStat.color = Color.black;
            uStat.finishNum = dfsFinish++;
            visitor.finish(u);
            top--;
        }
    }

    /**
       Visitor for working with DFS.
       <p>
       Depth first search recursively visits nodes according to the following psuedocode:
       The variable <tt>vis</tt> is a DFS.Visitor object.
       <pre>
       def {@link DFS#visit visit}(node u):
       {@link Visitor#discover vis.discover}(u)
       foreach edge e=(u,v) leaving u:
           {@link Visitor#startEdge vis.startEdge}(e)
           if (v is now being visited)
               {@link Visitor#backEdge vis.backEdge}(e)
           else if (v is not yet visited):
               {@link Visitor#treeEdge vis.treeEdge}(e)
               if {@link Visitor#follow follow}(v): visit(v)
           else if (v is already visited)
               if (v is a descended in the tree):
                   {@link Visitor#fwdEdge vis.fwdEdge}(e)
               else
                   {@link Visitor#crossEdge vis.crossEdge}(e)
           {@link Visitor#finishEdge vis.finishEdge}(e)
       {@link Visitor#finish vis.finish}(u)
       </pre>

       It is then invoked for an entire graph as follows
       <pre>
       forevery node n:
       if (not visited(n) and vis.follow(n):
           {@link DFS#visit visit}(n)
       </pre>
       </p>
    */
    public static class Visitor
    {
        /**
           Called when Node n is a root in the DFS.
        */
        public void root(Node n) {}
        
        /**
           Called when DFS first encounters a node during its search.
        */
        public void discover(Node n) {}
        
        /**
           Called when DFS is finished visiting a node, ie when
           all nodes reachable from <tt>n</tt> have been visited.
        */
        public void finish(Node n) {}
        
        /**
           Called when DFS first encounters an edge during its traversal.
        */
        public void startEdge(Edge e) {}
        
        /**
           Called when DFS is done visiting an edge.
        */
        public void finishEdge(Edge e) {}
        
        /**
           Called for every edge which leads to the {@link #discover discovery}
           of a node.
        */
        public void treeEdge(Edge e) {}
        
        /**
           Called for an edge which goes from a node <tt>n</tt> to a node
           <tt>t</tt> such that <tt>t</tt> is a descendent of <tt>n</tt>
           in the DFS tree.
        */
        public void fwdEdge(Edge e) {}

        /**
           Called for an edge which goes from a node <tt>n</tt> to a node
           <tt>a</tt> such that <tt>a</tt> is an ancestor of <tt>n</tt>
           in the DFS tree.
           
           This is the opposite of {@link #fwdEdge}.
        */
        public void backEdge(Edge e) {}
        
        /**
           Called for every edge from a node <tt>n</tt> to a node <tt>t</tt>
           such that <tt>t</tt> is reachable from <tt>n</tt> but is neither 
           a {@link #fwdEdge forward edge}, {@link #backEdge back edge}, 
           {@link #treeEdge tree edge}.
        */
        public void crossEdge(Edge e) {}
        
        /**
           If this method returns <tt>true</tt> the DFS algorithm stops.
        */
        public boolean done() { return false; }
        
        /**
           Return true iff the node <tt>n</tt> should be examined for outoing
           edges in the DFS algorithm.
        */
        public boolean follow(Node n) { return true; }
    }
}
