package stixar.graph.check;

import stixar.graph.Digraph;

/**
   A set of properties which may or may not hold for a given digraph.
 */
public enum DigraphProperty
{
    /** 
        Every vertex in the graph except one has exactly one incoming edge.  
        The exceptional vertex has no incoming edges.
    */
    Tree(new TreeChecker()), 
    /** 
        Same as {@link #Tree}, but more than one vertex may have 
        no incoming edges.
    */
    Forest(new ForestChecker()), 
    /**
       The graph contains no paths from a vertex to that same vertex.
     */
    CycleFree(new AcyclicChecker()), 
    /**
       For every edge <tt>(u,v)</tt> in the graph, there exists an 
       edge <tt>(v,u)</tt> in the graph.
    */
    Symmetric(new SymmetricChecker()), 
    /**
       The contains no edges <tt>(u,u)</tt> for any vertex <tt>u</tt>.
    */
    LoopFree(new LoopFreeChecker()), 
    /**
       The graph contains no edge
       <tt>(v,u)</tt> such that there is an edge <tt>(u,v)</tt>.
     */
    AntiSymmetric(new AntiSymmetricChecker()),

    /**
       There is at most one edge between every pair of vertices.
     */
    MultiEdgeFree(new MultiEdgeFreeChecker()),
    /**
       There exists a function <tt>f</tt> mapping vertices in the graph
       to points in a plan such that for every pair of edges 
       <tt>(u,v)</tt>,<tt>(u',v')</tt> in the 
       graph, the line segments going from <tt>f(u)</tt> to <tt>f(v)</tt>, 
       and from <tt>f(u')</tt> to <tt>f(v')</tt> do not cross.
     */
    Planar;

    protected DigraphChecker checker;

    DigraphProperty(DigraphChecker c)
    {
        this.checker = c;
    }

    // XXX delete me once complete.
    DigraphProperty()
    {
        this.checker = null;
    }

    /**
       Check whether or not a digraph satisfies the property.
     */
    public final boolean check(Digraph dg)
    {
        return checker.check(dg);
    }
}
