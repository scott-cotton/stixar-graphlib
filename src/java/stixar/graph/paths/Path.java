package stixar.graph.paths;

import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.BasicDigraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;

import stixar.util.CList;
import stixar.util.ListCell;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
   Path object.

   <pre>
   Path p;
   for (Edge e : path.edges())
       // ...
   for (Node n : path.nodes())
       // ...
   
   p.reverse();
   p.length();
   ...
   p.concat(p);
   p.concat(edge);
   p.prepend(p);
   p.prepend(edge);
   </pre>
 */
public class Path implements Cloneable
{
    protected CList<Edge> edges;
    protected Node head;
    protected Node tail;

    /**
       Construct a path from an array of edges.
     */
    public Path(Edge[] edgeA)
    {
        this.edges = new CList<Edge>();
        for (Edge e : edgeA)
            edges.append(e);
        if (!edges.isEmpty()) {
            this.head = edges.getFirst().source();
            this.tail = edges.getLast().target();
        }
    }

    public Path(Node head)
    {
        this();
        this.head = head;
        this.tail = head;
    }

    /**
       Construct an empty path.
     */
    public Path()
    {
        this.edges = new CList<Edge>();
        this.head = null;
        this.tail = null;
    }

    /**
       Construct a path from an edge list.
     */
    public Path(List<Edge> edgeList)
    {
        this.edges = new CList<Edge>();
        edges.addAll(edgeList);
        if (!edges.isEmpty()) {
            this.head = edges.getFirst().source();
            this.tail = edges.getLast().target();
        }
    }

    /**
       Make a shallow copy of a path.
       @param p the path to copy.
     */
    public Path(Path p)
    {
        this(p.edges);
    }

    /**
       Clone this path by way of shallow copy.
     */
    public Path clone()
    {
        return new Path(this);
    }

    /**
       Return the first node in the path.
     */
    public Node head()
    {
        return head;
    }

    /**
       return this last node in the path.
    */
    public Node tail()
    {
        return tail;
    }

    /**
       Append an edge to this path.
       @param e the edge to append
       @throws IllegalArgumentException if the path is 
       not empty, ends with an edge <tt>pe</tt> and <tt>e.source().equals(pe.target())</tt>
       does not hold.
     */
    public void append(Edge e)
    {
        if (!edges.isEmpty()) {
            if (!edges.last().target().equals(e.source()))
                throw new IllegalArgumentException();
        }
        edges.append(e);
        tail = e.target();
        if (head == null || head == tail) head = e.source();
    }

    /**
       Append a path to this path.
       The appended path has zero length after the append operation.
       This operation takes constant time.

       @param p the path to append.
       @throws IllegalArgumentException if the result is not a
       valid path.
     */
    public void append(Path p)
    {
        if (!edges.isEmpty() && !p.edges.isEmpty()) {
            Edge last = edges.last();
            Edge first = p.edges.first();
            if (!last.target().equals(first.source())) {
                throw new IllegalArgumentException();
            }
            head = first.source();
            tail = last.target();
        }
        edges.append(p.edges);
        tail = p.tail;
        if ((head == null || head == tail) && !edges.isEmpty()) {
            head = edges.getFirst().source();
        }
    }

    /**
       Prepend an edge to this path.
       @param e the edge to prepend
       @throws IllegalArgumentException if the path is 
       not empty, begins with an edge <tt>pb</tt> and <tt>e.target().equals(pb.source())</tt>
       does not hold.
     */
    public void prepend(Edge e)
    {
        if (!edges.isEmpty()) {
            Edge first = edges.first();
            if (!first.source().equals(e.target())) {
                throw new IllegalArgumentException();
            }
        }
        edges.prepend(e);
        head = e.source();
        if (tail == null || tail == head) tail = e.target();
    }

    /**
       Prepend a path <tt>p</tt> to this path, destroying <tt>p</tt>
       in the process.
       At the end of this method, <tt>p</tt> is an empty path.

       @param p the path to prepend
       @throws IllegalArgumentException if the operation would
       result in an invalid path.
     */
    public void prepend(Path p)
    {
        if (!edges.isEmpty() && !p.edges.isEmpty()) {
            Edge last = p.edges.last();
            Edge first = edges.first();
            if (!last.target().equals(first.source())) {
                throw new IllegalArgumentException();
            }
        }
        edges.prepend(p.edges);
        head = p.head;
        if (tail == null || tail == head) tail = p.tail;
    }

    /**
       Return true if and only if this path is a cycle.
     */
    public boolean isCycle()
    {
        if (!edges.isEmpty()) {
            return head == tail;
        }
        return false;
    }

    /**
       Return true iff this path is a simple path, that is 
       if every Node in the path is visited exactly once.
     */
    public boolean isSimple()
    {
        HashMap<Node,Integer> adjCounts = new HashMap<Node,Integer>(edges.size());
        for (Edge e : edges) {
            Node s = e.source();
            Integer sAdj = adjCounts.get(s);
            if (sAdj == null) {
                adjCounts.put(s, 1);
            } else if (sAdj == 2) {
                return false;
            } else {
                adjCounts.put(s, sAdj + 1);
            }
            Node t = e.source();
            Integer tAdj = adjCounts.get(t);
            if (tAdj == null) {
                adjCounts.put(t, 1);
            } else if (tAdj == 2) {
                return false;
            } else {
                adjCounts.put(t, sAdj + 1);
            }
        }
        return !isCycle();
    }

    /**
       Iterate over the nodes in this path.
     */
    public Iterable<Node> nodes()
    {
        return new Iterable<Node> ()
        {
            public Iterator<Node> iterator()
            {
                return new PathNodeIterator(edges.firstCell(), head);
            }
        };
    }

    /**
       Return the set of nodes found in this path.
     */
    public Set<Node> nodeSet()
    {
        HashSet<Node> nodeSet = new HashSet<Node>();
        for (Node n : nodes()) {
            nodeSet.add(n);
        }
        return nodeSet;
    }

    /**
       Return the sequence of edges in this path in the form of 
       a {@link CList}.

       @return a {@link CList} consisting of the sequence of edges
       of this path in order.
     */
    public CList<Edge> edges()
    {
        return edges;
    }

    /**
       Return the set of edges in this path.
     */
    public Set<Edge> edgeSet()
    {
        HashSet<Edge> edgeSet = new HashSet<Edge>(edges.size());
        edgeSet.addAll(edges);
        return edgeSet;
    }

    /**
       Return the number of edges in this path.
     */
    public int length()
    {
        return edges.size();
    }

    /**
       Construct a new {@link BasicDigraph} from this path.
       @return a digraph which contains a topologic copy of all
       the nodes and edges in this path.
     */
    public BasicDigraph digraph()
    {
        HashMap<Node,CList<Edge>> outMap = new HashMap<Node,CList<Edge>>(edges.size());
        HashMap<Node,Integer> idMap = new HashMap<Node,Integer>(edges.size());
        HashMap<Integer,Node> revIdMap = new HashMap<Integer,Node>(edges.size());
        HashSet<Edge> edgeSet = new HashSet<Edge>(edges.size());
        int ttlNodes = 0;
        for (Edge e : edges) {
            if (edgeSet.contains(e)) 
                continue;
            edgeSet.add(e);
            Node s = e.source();
            CList<Edge> outEdges = outMap.get(s);
            if (outEdges == null) {
                outEdges = new CList<Edge>();
                outMap.put(s, outEdges);
                idMap.put(s, ttlNodes);
                revIdMap.put(ttlNodes++, s);
            }
            outEdges.add(e);
        }
        BasicDigraph digraph = new BasicDigraph(ttlNodes, edgeSet.size());
        digraph.genNodes(ttlNodes);
        for (int i=0; i<ttlNodes; ++i) {
            Node key = revIdMap.get(i);
            Node newS = digraph.node(i);
            CList<Edge> outEdges = outMap.get(key);
            for (Edge e : outEdges) {
                Node t = e.target();
                int tid = idMap.get(t);
                Node newT = digraph.node(tid);
                digraph.genEdge(newS, newT);
            }
        }
        return digraph;
    }

    /**
       Make a human readable string representation.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Path[");
        for (ListCell<Edge> cell = edges.firstCell(); cell != null && cell.isValid(); cell = cell.next()) {
            sb.append(cell.value().source().nodeId());
            if (cell.next() != null && cell.next().isValid())
                sb.append(" ");
            else
                sb.append(" " + cell.value().target().nodeId());
        }
        if (edges.isEmpty() && head != null) {
            sb.append(head.nodeId());
        }
        sb.append("]\n");
        return sb.toString();
    }
}

class PathNodeIterator implements Iterator<Node>
{
    protected ListCell<Edge> edgeCell;
    protected Node node;
    
    PathNodeIterator(ListCell<Edge> edgeCell, Node head)
    {
        this.edgeCell = edgeCell;
        if (edgeCell != null) {
            node = edgeCell.value().source();
        } else {
            node = head;
        }
    }

    public final boolean hasNext()
    {
        return node != null;
    }

    public final Node next()
    {
        Node result = node;
        if (edgeCell != null && edgeCell.isValid()) {
            node = edgeCell.value().target();
            edgeCell = edgeCell.next();
        } else {
            node = null;
        }
        return result;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}

