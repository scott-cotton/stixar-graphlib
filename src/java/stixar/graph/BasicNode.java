package stixar.graph;

import stixar.graph.attr.AttributableBase;
import stixar.util.CList;
import stixar.util.ListCell;

import java.util.Comparator;
import java.util.Arrays;
/**
   Basic Nodes, in case the use user doesn't want to roll their own.
 */
public class BasicNode extends AttributableBase
    implements Node
{
    protected int id;

    protected CList<BasicEdge> out;
    protected CList<BasicEdge> in;

    protected BasicDigraph digraph;

    public BasicNode(BasicDigraph dg, int id)
    {
        super(id);
        this.digraph = dg;
        this.id = id;
        this.out = new CList<BasicEdge>();
        this.in = new CList<BasicEdge>();
    }

    /*
      Javadoc'd in Node.
     */
    public final int nodeId() 
    { 
        return id; 
    }

    final int nodeId(int i)
    {
        super.setAttrIndex(i);
        return id = i;
    }

    /*
      Javadoc'd in Node
     */
    public final BasicEdge out()
    {
        return out.peek();
    }

    /**
       Return the first incoming edge to this node.
     */
    public final BasicEdge in()
    {
        return in.peek();
    }

    /**
      Internal method, do not use.
      @see MutableGraph
     */
    public void add(BasicEdge e)
    {
        e.outCell = out.append(e);
        e.inCell = e.target().in.append(e);
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    public void remove(BasicEdge e)
    {
        out.remove(e.outCell);
        e.target().in.remove(e.inCell);
    }

    /**
       Return the number of edges leading from this node.
     */
    public final int degree()
    {
        return out.size();
    }

    /**
       Rerturn the number of edges leading to this node.
     */
    public final int inDegree()
    {
        return in.size();
    }

    /**
       Returns the digraph to which this basic node belongs.
     */
    public final BasicDigraph digraph()
    {
        return digraph;
    }

    /**
       Human readable string.
     */
    public String toString()
    {
        return "BasicNode(" + id + ")";
    }

    /**
       Produce a hash code for this node.
     */
    public int hashCode()
    {
        return id;
    }

    /**
       Implement equals so that hashing works.
     */
    public boolean equals(Object o)
    {
        if (o instanceof BasicNode)
            return id == ((BasicNode) o).id;
        return false;
    }

    /*
      package local helper for sorting edges.
     */
    BasicEdge[] sortEdges(Comparator<Edge> cmp, BasicEdge[] ea)
    {
        if (ea.length < out.size())
            ea = new BasicEdge[out.size()];
        int i=0;
        for (BasicEdge e : out) ea[i++] = e;
        Arrays.sort(ea, 0, out.size(), cmp);
        ListCell<BasicEdge> it = out.firstCell();
        i = 0;
        while(it != null) {
            it.value(ea[i++]);
            it = it.next();
        }
        return ea;
    }
}
