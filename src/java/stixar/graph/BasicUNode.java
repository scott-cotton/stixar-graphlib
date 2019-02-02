package stixar.graph;

import stixar.graph.attr.AttributableBase;
import stixar.util.CList;
import stixar.util.ListCell;

import java.util.Comparator;
import java.util.Arrays;

/**
   Implementation of a node for an undirected graph.
 */
public class BasicUNode extends AttributableBase
    implements Node
{
    CList<BasicUEdge> edges;
    protected BasicUGraph ugraph;
    protected int nodeId;

    public BasicUNode(BasicUGraph ugraph, int id)
    {
        super(id);
        nodeId = id;
        this.ugraph = ugraph;
        edges = new CList<BasicUEdge>();
    }

    /**
       Return the graph with which this node is associated.
       @return the graph with which this node is associated. 
     */
    public final UGraph ugraph()
    {
        return ugraph;
    }

    public final int nodeId()
    {
        return nodeId;
    }

    final int nodeId(int i)
    {
        super.setAttrIndex(i);
        return nodeId = i;
    }

    /**
       Return the number of edges adjacent to this node.
       @return the number of edges adjacent to this node. 
     */
    public final int degree()
    {
        return edges.size();
    }

    public BasicUEdge out()
    {
        return edges.peek();
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    void add(BasicUEdge e)
    {
        BasicUEdge rev = e.reverse();
        if (e.source() == this) {
            e.sCell = edges.append(e);
            rev.sCell = e.target().edges.append(rev);
        } else if (e.target() == this) {
            rev.sCell = edges.append(e);
            e.sCell = e.source().edges.append(e);
        } else {
            throw new IllegalArgumentException("attempt to add an unrelated edge");
        }
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    void remove(BasicUEdge e)
    {
        BasicUEdge rev = e.reverse();
        if (e.source() == this) {
            edges.remove(e.sCell);
            rev.source().edges.remove(rev.sCell);
        } else if (e.target() == this) {
            edges.remove(rev.sCell);
            e.source().edges.remove(e.sCell);
        } else {
            throw new IllegalArgumentException("attempt to add an unrelated edge");
        }
    }

    /**
       Produce a hash code for this node.
       @return a suitable integer for hashing.
     */
    public final int hashCode()
    {
        return nodeId;
    }

    /**
       Test for equality.
       @param o the object against which to test for equality.
       @return true iff this node is the same node as <tt>o</tt>
     */
    public final boolean equals(Object o)
    {
        if (o instanceof BasicUNode) {
            BasicUNode n = (BasicUNode) o;
            return nodeId == n.nodeId && ugraph == n.ugraph;
        }
        return false;
    }


    /*
      package local helper for sorting edges.
     */
    BasicUEdge[] sortEdges(Comparator<Edge> cmp, BasicUEdge[] ea)
    {
        if (ea.length < edges.size())
            ea = new BasicUEdge[edges.size()];
        int i=0;
        for (BasicUEdge e : edges) ea[i++] = e;
        Arrays.sort(ea, 0, edges.size(), cmp);
        ListCell<BasicUEdge> it = edges.firstCell();
        i = 0;
        while(it != null) {
            it.value(ea[i++]);
            it = it.next();
        }
        return ea;
    }
    /**
       Produce a human readable string.
     */
    public String toString()
    {
        return String.format("BasicUNode(%d)", nodeId);
    }
}
