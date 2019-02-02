package stixar.graph;

import stixar.graph.attr.AttributableBase;

import stixar.util.ListCell;

/**
   Basic Edges, in case the use user doesn't want to roll their own.
 */
public class BasicEdge extends AttributableBase
    implements Edge
{
    protected BasicNode source;
    protected BasicNode target;

    ListCell<BasicEdge> outCell;
    ListCell<BasicEdge> inCell;

    protected BasicDigraph digraph;

    protected int eid;

    /**
       Construct a BasicEdge with an identifier and a 
       source and target node.
       
       @param digraph the digraph to which this edge belongs.
       @param id the identifier for the edge.
       @param s the source node.
       @param t the target node.
     */
    public BasicEdge(BasicDigraph digraph, int id, BasicNode s, BasicNode t)
    {
        super(id);
        this.digraph = digraph;
        this.eid = id;
        this.source = s;
        this.target = t;
    }

    /**
       Return the identifier for this edge.
     */
    public final int edgeId() 
    { 
        return eid; 
    }

    final int edgeId(int i)
    {
        super.setAttrIndex(i);
        return eid = i;
    }

    /**
       Return the next edge in the list of edges coming from
       the {@link #source}.
     */
    public final BasicEdge next() 
    { 
        ListCell<BasicEdge> c = outCell.next();
        return c == null ? null : c.value();
    }

    
    /**
       Return the next edge in the list of edges leading into the target
       of this edge.
     */
    public final BasicEdge nextIn()
    {
        ListCell<BasicEdge> c = inCell.next();
        return c == null ? null : c.value();
    }

    /**
       Return the previous edge in the list of edges coming from
       the {@link #source}.
     */    
    public BasicEdge prev() 
    { 
        ListCell<BasicEdge> c = outCell.prev();
        return c == null ? null : c.value();
    }

    /**
       Return the previous edge in the linked list of edges leading into the
       target of this edge.
     */
    public BasicEdge prevIn()
    {
        ListCell<BasicEdge> c = inCell.next();
        return c == null ? null : c.value();
    }

    /**
       Return the source node of this edge.
     */
    public final BasicNode source() 
    { 
        return source; 
    }

    /**
       Set and return the source of this node.
     */
    public final BasicNode source(BasicNode n)
    {
        return source = n;
    }

    /**
       Return the target node of this edge.
     */
    public final BasicNode target() 
    { 
        return target; 
    }

    /**
       Set and return the target node.
     */
    public final BasicNode target(BasicNode n)
    {
        return target = n;
    }

    /**
       Produce a human readable string representation.
     */
    public String toString()
    {
        return "BasicEdge-" + edgeId() + "(" + source.nodeId() + "," + target.nodeId() +  ")";
    }

    /**
       Produce a hash code for this edge.
     */
    public int hashCode()
    {
        return eid;
    }

    /**
       Make hashing work.
     */
    public boolean equals(Object o)
    {
        if (o instanceof BasicEdge) {
            return ((BasicEdge) o).eid == eid;
        }
        return false;
    }
}
