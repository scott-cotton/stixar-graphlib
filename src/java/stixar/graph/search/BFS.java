package stixar.graph.search;

import stixar.graph.Graph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.GraphFilter;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.GraphFilter;

import stixar.graph.order.NodeOrder;

import java.util.LinkedList;
import java.util.Arrays;

/**
   Breadth first search filtering algorithm.

   @see BFS.Visitor
 */
public class BFS implements Algorithm, Filtering
{
    /**
       The different status' of nodes during the execution 
       of breadth first search.
     */
    public enum Color { /** not yet visited */white, /** queued */ grey,  
            /** already visited and not in queue */ black }

    protected Graph graph;
    protected GraphFilter filt;

    protected Visitor visitor;

    protected Color[] colors;
    protected int[] bfsNumbers;
    protected int bfsNum;

    protected LinkedList<Node> queue;

    /**
       Construct a BFS algorithm for a graph <tt>graph</tt> 
       with the visitor <tt>vis</tt>.
     */
    public BFS(Graph graph, Visitor vis)
    {
        this.graph = graph;
        this.filt = graph.getFilter();
        this.colors = new Color[graph.nodeAttrSize()];
        this.bfsNumbers = new int[graph.nodeAttrSize()];
        this.bfsNum = 0;
        this.visitor = vis;
        Arrays.fill(colors, Color.white);
        queue = new LinkedList<Node>();
    }

    /**
       Set the color of the node <tt>node</tt> to <tt>color</tt>.
     */
    public void color(Node node, Color color)
    {
        colors[node.nodeId()] = color;
    }

    /**
       Return the visitor.
     */
    public Visitor visitor() 
    { 
        return visitor; 
    }

    /**
       Return and set the visitor.
     */
    public Visitor visitor(Visitor v) 
    { 
        return visitor = v; 
    }

    /**
       Run the algorithm.
     */
    public void run()
    {
        for (Node n: graph.nodes()) {
            if (visitor.done()) break;
            if (filt != null && filt.filter(n)) continue;
            if (colors[n.nodeId()] == Color.white && visitor.follow(n)) {
                visitor.root(n);
                visit(n);
            }
        }
    }

    /**
       Return true iff the node <tt>n</tt> has been visited.
     */
    public final boolean visited(Node n)
    {
        return colors[n.nodeId()] != Color.white;
    }

    /**
       Visit the node <tt>n</tt>.
     */
    public final void visit(Node n)
    {
        colors[n.nodeId()] = Color.grey;
        bfsNumbers[n.nodeId()] = bfsNum++;
        queue.addLast(n);
        visitor.discover(n);
        while(queue.size() > 0) {
            if (visitor.done()) break;
            Node u = queue.removeFirst();
            if (filt != null && filt.filter(u)) continue;
            colors[u.nodeId()] = Color.black;
            visitor.start(u);
            for(Edge e = u.out(); e != null; e = e.next()) {
                if (visitor.done()) break;
                if (filt != null && filt.filter(e)) {
                    continue;
                }
                Node v = e.target();
                switch (colors[v.nodeId()])
                    {
                    case white:
                        visitor.treeEdge(e);
                        if (visitor.done()) break;
                        if (visitor.follow(v)) {
                            colors[v.nodeId()] = Color.grey;
                            bfsNumbers[v.nodeId()] = bfsNum++;
                            queue.addLast(v);
                            visitor.discover(v);
                        }
                        break;
                    case grey:
                        visitor.queueEdge(e);
                    case black:
                        visitor.crossEdge(e);
                    }
            }
            visitor.finish(u);
        }
    }

    public NodeOrder order()
    {
        return new NodeOrder(graph, bfsNumbers);
    }

    /**
       Reset the algorithm.
     */
    public void reset()
    {
        Arrays.fill(colors, Color.white);
        Arrays.fill(bfsNumbers, -1);
        queue.clear();
    }

    /**
       Visitor for Breadth first search algorithm.
    */
    public static class Visitor
    {

        /** Called when a node is a root in the BFS */
        public void root(Node n) {}
        
        /** Called when a node is first discovered via breadth first search */
        public void discover(Node n) {}
        
        /** Called just after a node's edges are processed */
        public void finish(Node n) {}
        
        /** Called just before a node's edges are processed */
        public void start(Node n) {}
        
        /** Called for an edge to a discovered node */
        public void treeEdge(Edge e) {}
        
        /** Called for an edge other than a tree edge */
        public void crossEdge(Edge e) {}
        
        public void queueEdge(Edge e) {}
        
        /** 
            Tell BFS whether or not to follow the node <tt>n</tt>
        */
        public boolean follow(Node n) { return true; }
        
        /** trigger the {@link BFS} to halt if return true. */
        public boolean done() { return false; }
    }
}
