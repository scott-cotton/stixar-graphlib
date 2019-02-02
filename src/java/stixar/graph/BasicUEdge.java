package stixar.graph;

import stixar.graph.attr.AttributableBase;

import stixar.util.ListCell;

/**
   Implementation of an edge for an undirected graph.
 */
/*
  The basic idea behine this class is that in fact every undirected
  edge is comprised of two distinct half-edges. Each half edge 
  points to the other half edge in the couple by means of its
  "reverse" attribute.  Each half edge stores a pointer to its
  source vertex, and thus target() becomes .reverse.source.
  This representation is a relatively easy way to implement the 
  demands of the Edge and Node interfaces for the undirected case.

  While directed edges can be implemented similarly, tests show
  that there is a small but noticable performance hit as a result.
  Hence we use this representation only in the undirected case.
 */
public class BasicUEdge extends AttributableBase
    implements Edge
{
    protected BasicUGraph ugraph;
    protected int eid;
    protected BasicUNode source;
    protected ListCell<BasicUEdge> sCell;
    protected BasicUEdge reverse;

    protected BasicUEdge(BasicUGraph ugraph, BasicUNode source, BasicUEdge reverse, int id)
    {
        super(id);
        this.ugraph = ugraph;
        this.source = source;
        this.reverse = reverse;
        this.eid = id;
        this.sCell = source.edges.append(this);
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    public static BasicUEdge getBasicUEdge(BasicUGraph ugraph, BasicUNode source, BasicUNode target, int id)
    {
        BasicUEdge e = new BasicUEdge(ugraph, source, null, id);
        BasicUEdge r = new BasicUEdge(ugraph, target, e, id);
        e.reverse = r;
        return e;
    }

    public final BasicUEdge next()
    {
        ListCell<BasicUEdge> c = sCell.next();
        if (c != null)
            return c.value();
        return null;
    }

    public final int edgeId()
    {
        return eid;
    }

    final int edgeId(int i)
    {
        super.setAttrIndex(i);
        return eid = i;
    }

    public final BasicUNode source()
    {
        return source;
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    public final BasicUNode source(BasicUNode n)
    {
        return source = n;
    }
    
    public final BasicUNode target()
    {
        return reverse.source();
    }

    /**
       Internal method, do not use.
       @see MutableGraph
     */
    public final BasicUNode target(BasicUNode n)
    {
        return reverse.source(n);
    }

    /**
       Internal method.
     */
    public final BasicUEdge reverse()
    {
        return reverse;
    }

    /**
       Produce a hash code for this edge.
     */
    public final int hashCode()
    {
        return eid;
    }

    /**
       Test for equality.
     */
    public final boolean equals(Object o)
    {
        if (o instanceof BasicUEdge) {
            BasicUEdge e = (BasicUEdge) o;
            return eid == e.eid && ugraph == e.ugraph;
        }
        return false;
    }
    
    /**
       Produce a human readable string.
     */
    public final String toString()
    {
        return String.format("BasicUEdge%d(%d,%d)", eid, source().nodeId(),
                             target().nodeId());
    }
}
