package stixar.graph;

import stixar.graph.order.NodeOrder;

import stixar.util.CList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Arrays;

/**
   An implementation of an editable Digraph with
   {@link BasicUNode}s and {@link BasicUEdge}s.
 */
public class BasicUGraph extends FilterGraph
    implements MutableUGraph
{
    protected BasicUNode[] nodes;
    protected int nodeCount;
    protected int edgeCount;
    // for fail-fast iterators.
    protected int nMods;
    protected int eMods;
    
    /*
      Construct a new BasicDigraph with nodes specified.

      performed by the BasicDGFactory.
     */
    public BasicUGraph(BasicUNode[] nodes)
    {
        super(nodes.length, 0);
        this.nodes = nodes;
        this.nodeCount = 0;
        this.edgeCount = 0;
        this.nMods = 0;
        this.eMods = 0;
        for (BasicUNode n : nodes) {
            if (n == null) continue;
            nodeCount++;
            nSlots.set(n.nodeId(), true);
            for (BasicUEdge e = n.out(); e != null; e = e.next()) {
                edgeCount++;
                eSlots.set(e.edgeId(), true);
            }
        }
    }

    /**
       Construct a new undirected graph with room for <tt>n</tt> nodes
       and whose edge attribute mechanisms will reserve room for <tt>m</tt>
       edges.
       @param n the node capacity of the graph.
       @param m the edge-attribute capacity of the graph.
     */
    public BasicUGraph(int n, int m)
    {
        super(n, m);
        this.nodes = new BasicUNode[n];
        this.nodeCount = 0;
        this.edgeCount = 0;
        this.nMods = 0;
        this.eMods = 0;
    }
    
    public BasicUGraph()
    {
        this(12,38);
    }

    /*
      javadoc'd in digraph, more or less.
     */
    public BasicUNode node(int id)
    {
        return nodes[id];
    }

    /**
       Produce an undirected node.
     */
    public BasicUNode genNode()
    {
        if (nodeCap <= nodeTop + 1) {
            growNodes(nodeCap * 2);
        }
        BasicUNode n = new BasicUNode(this, nodeTop);
        nSlots.set(nodeTop, true);
        nodeCount++;
        nodes[nodeTop++] = n;
        nMods++;
        return n;
    }

    public void remove(Node n)
    {
        if (!(n instanceof BasicUNode)) {
            throw new IllegalArgumentException();
        }
        BasicUNode bn = (BasicUNode) n;
        CList<BasicUEdge> edges = new CList<BasicUEdge>();
        for(BasicUEdge e = bn.out(); e != null; e = e.next()) {
            edges.add(e);
        }
        for (BasicUEdge e : edges) {
            remove(e);
        }
        nSlots.set(bn.nodeId(), false);
        nodes[bn.nodeId()] = null;
        nodeCount--;
        super.remove(bn);
        bn.nodeId(-1);
        nMods++;
    }

    public void remove(Edge e)
    {
        if (!(e instanceof BasicUEdge)) {
            throw new IllegalArgumentException();
        }
        BasicUEdge be = (BasicUEdge) e;
        int eid = be.edgeId();
        be.source().remove(be);
        eSlots.set(eid, false);
        BasicUEdge rev = be.reverse();
        super.remove(be);
        rev.edgeId(-eid - 1);
        be.edgeId(-eid - 1);
        edgeCount--;
        eMods++;
    }

    public void relink(Edge e)
    {
        if (!(e instanceof BasicUEdge)) {
            throw new IllegalArgumentException();
        }
        BasicUEdge be = (BasicUEdge) e;
        int eid = be.edgeId();
        be.source().add(be);
        be.edgeId(-eid - 1);
        BasicUEdge rev = be.reverse();
        rev.edgeId(-eid - 1);
        edgeCount++;
        eMods++;
    }

    public List<Node> genNodes(int n)
    {
        CList<Node> result = new CList<Node>();
        for (int i=0; i<n; ++i)
            result.add(genNode());
        return result;
    }

    /**
       Generate a new edge between nodes <tt>u</tt> and <tt>v</tt>.
       <p>
       If the returned edge object is <tt>e</tt>, <tt>e.source()</tt>
       will return <tt>u</tt> and <tt>e.target()</tt> will return <tt>v</tt>.
       </p>

       @param u One node to connect via an undirected edge.
       @param v Another, distinct node to connect via an undirected edge.
       @return a new undirected edge connecting <tt>u</tt> and <tt>v</tt>.

       @throws IllegalArgumentException if <tt>u == v</tt> or if 
       either <tt>u</tt> or <tt>v</tt> are not instances of {@link BasicUNode},
       or if <tt>u.ugraph()</tt> or <tt>v.ugraph()</tt> is not this graph.
     */
    public BasicUEdge genEdge(Node u, Node v)
    {
        if (!(u instanceof BasicUNode) || !(v instanceof BasicUNode)) {
            throw new IllegalArgumentException
                ("invalid class for a node in genEdge");
        }
        if (u == v) {
            throw new IllegalArgumentException
                ("attempt to create self loop in undirected graph");
        }
        BasicUNode bu = (BasicUNode) u;
        BasicUNode bv = (BasicUNode) v;
        if (bu.ugraph() != this || bv.ugraph() != this)
            throw new IllegalArgumentException();
        BasicUEdge res = BasicUEdge.getBasicUEdge(this, bu, bv, -1);
        if (edgeCap <= edgeTop + 1) {
            growEdges(edgeCap * 2);
        }
        res.edgeId(edgeTop);
        res.reverse().edgeId(edgeTop++);
        super.newEdge(res);
        edgeCount++;
        eMods++;
        return res;
    }

    public void ensureCapacity(int n, int m)
    {
        if (nodes.length < n ) {
            growNodes(n);
        }
        if (edgeCap < m) {
            growEdges(m);
        }
    }

    protected void growNodes(int newCap)
    {
        BasicUNode[] newNodes = new BasicUNode[newCap];
        System.arraycopy(nodes, 0, newNodes, 0, nodeCap);
        nodes = newNodes;
        super.growNodes(newCap);
    }

    public void moveEdge(Edge e, Node u, Node v)
    {
        if (!(e instanceof BasicUEdge) || !(u instanceof BasicUNode)
            || !(v instanceof BasicUNode)) {
            throw new IllegalArgumentException("invalid class for moveEdge");
        }
        BasicUEdge be = (BasicUEdge) e;
        BasicUNode bu = (BasicUNode) u;
        BasicUNode bv = (BasicUNode) v;
        be.source().remove(be);
        be.source(bu);
        be.target(bv);
        bu.add(be);
        eMods++;
    }

    public void sortEdges(Comparator<Edge> cmp)
    {
        BasicUEdge[] ea = new BasicUEdge[nodes.length];
        for (BasicUNode bn : nodes) {
            ea = bn.sortEdges(cmp, ea);
        }
    }

    /*
      Javadoc'd in digraph.
     */
    public int nodeSize()
    {
        return nodeCount;
    }

    /*
      Javadoc'd in digraph.
     */
    public int nodeAttrSize()
    {
        return nodeTop;
    }

    /**
       Returns an appropriate size for an edge attribute array.
     */
    public int edgeSize()
    {
        return edgeCount;
    }

    public int edgeAttrSize()
    {
        return edgeTop;
    }

    /*
      Javadoc'd in Graph
     */
    public Iterable<Node> nodes()
    {
        return new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new BasicUNodeIterator(nodes, nMods);
            }
        };
    }




    /*
      Javadoc'd in Graph
     */
    public Iterable<Node> nodes(final NodeOrder order)
    {
        int[] perm = order.permutation();
        ArrayList<Node> res = new ArrayList<Node>(nodeCount);
        if (order.reversed()) {
            for (int i=perm.length - 1; i>=0; --i)
                if (perm[i] != -1) {
                    Node n = nodes[perm[i]];
                    assert n != null;
                    res.add(n);
                } 
        } else {
            for (int i=0; i<perm.length; ++i) {
                if (perm[i] != -1) {
                    Node n = nodes[perm[i]];
                    assert n != null;
                    res.add(n);
                }
            }
        }
        return res;
    }

    /*
      Javadoc'd in Graph
     */
    public Iterable<Edge> edges()
    {
        return new Iterable<Edge>() {
            public Iterator<Edge> iterator() {
                return new BasicUEdgeIterator(nodes, eMods);
            }
        };
    }


    /**
       Produces a string for the graph in a simple adjacency list 
       representation.  Each node <tt>n</tt> is listed in order (by {@link Node#nodeId}), 
       followed by  a colon ':' and a space separated list of the nodes
       to which <tt>n</tt> is linked by an edge.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Node n : nodes()) {
            sb.append(n.nodeId() + ":");
            for (Edge e = n.out(); e != null; e = e.next()) {
                sb.append(" " + e.target().nodeId());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void clear()
    {
        super.clear();
        Arrays.fill(nodes, null);
        edgeCount = nodeCount = 0;
        edgeTop = nodeTop = 0;
        eSlots.clear();
        nSlots.clear();
        eMods++;
        nMods++;
    }



    public void trimToSize()
    {
        if (nodeCount < nodeTop || nodeTop < nodeCap) {
            _shrinkNodes(nodeCount);
        }
        if (edgeCount < edgeTop || edgeTop < edgeCap) {
            _shrinkEdges(edgeCount);
        }
    }

    protected void _shrinkEdges(int newCap)
    {
        int[] perm = super.shrinkEdges(newCap);
        eSlots.clear();
        for (int i=0; i<nodeTop; ++i) {
            BasicUNode n = nodes[i];
            if (n == null) continue;
            for (BasicUEdge f = n.out(); f != null; f = f.next()) {
                int oid = f.edgeId();
                int nid = perm[oid];
                f.edgeId(nid);
                eSlots.set(nid, true);
            }
        }
    }

    protected void _shrinkNodes(int newCap)
    {
        int[] perm = shrinkNodes(newCap);
        nSlots.clear();
        BasicUNode[] newNodes = new BasicUNode[newCap];
        for (BasicUNode n : nodes) {
            if (n == null) continue;
            int oid = n.nodeId();
            int nid = perm[oid];
            n.nodeId(nid);
            nSlots.set(nid, true);
            newNodes[nid] = n;
        }
        nodes = newNodes;
    }



    
    class BasicUNodeIterator implements Iterator<Node>
    {
        protected BasicUNode[] nodes;
        protected int index;
        protected int expectedMods;

        BasicUNodeIterator(BasicUNode[] nodes, int nMods)
        {
            this.nodes = nodes;
            this.index = nextIndex(0);
            this.expectedMods = nMods;
        }
        
        public boolean hasNext()
        {
            if (expectedMods != nMods)
                throw new ConcurrentModificationException();
            return index < nodes.length;
        }
        
        public Node next()
        {
            if (expectedMods != nMods)
                throw new ConcurrentModificationException();
            BasicUNode result = nodes[index];
            index = nextIndex(index + 1);
            return result;
        }
        
        protected int nextIndex(int start)
        {
            int res = start;
            while(res < nodes.length && nodes[res] == null) res++;
            return res;
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
    
    // 
    // XXX broken, iterates over each edge twice.
    //
    class BasicUEdgeIterator implements Iterator<Edge>
    {
        protected BasicUNode[] nodes;
        protected int index;
        protected Edge edge;
        protected int expectedMods;
        
        BasicUEdgeIterator(BasicUNode[] nodes, int eMods)
        {
            this.nodes = nodes;
            this.expectedMods = eMods;
            this.edge = null;
            this.index = nextNodeIndex(0);
            if (index < nodes.length) {
                edge = nodes[index].out();
            }
            this.edge = nextEdge(edge, index);
        }
        
        
        public boolean hasNext()
        {
            if (expectedMods != eMods)
                throw new ConcurrentModificationException();
            return edge != null;
        }
        
        public Edge next()
        {
            if (expectedMods != eMods)
                throw new ConcurrentModificationException();
            Edge result = edge;
            edge = edge.next();
            edge = nextEdge(edge, index);
            return result;
        }

        // find next node index >= i.
        protected int nextNodeIndex(int i)
        {
            int res = i;
            while(res < nodes.length && nodes[res] == null) res++;
            return res;
        }

        // edge e may be null.
        protected boolean trueEdge(Edge e)
        {
            return e != null && e.target().nodeId() > e.source().nodeId();
        }

        /*
          Pre: index is current valid index
          Edge: is any edge, or null.
         */
        protected Edge nextEdge(Edge e, int idx)
        {
            Edge res = e;
            while(!trueEdge(res) && idx < nodes.length) {
                if (res != null) {
                    res = res.next();
                    continue;
                } else {
                    idx = nextNodeIndex(idx + 1);
                    if (idx < nodes.length)
                        res = nodes[idx].out();
                }
            }
            this.index = idx;
            return res;
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
