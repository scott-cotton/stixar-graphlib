package stixar.graph.check;

import stixar.graph.UGraph;

/**
   A set of properties which may or may not hold for a given undirected graph.
 */
public enum UGraphProperty
{
    /** 
        The graph <tt>(V,E)</tt> has <tt>|E| = |V| - 1</tt> and 
        no cycles.
    */
    Tree(new UTreeChecker()), 
    /** 
        Graph is acyclic and has at most <tt>|V| - 1</tt> edges.
    */
    Forest(new UForestChecker()), 

    /**
       The graph contains no multiple edges.
     */
    MulitEdgeFree(new MultiEdgeFreeChecker()),
        
    /**
       There exists a function <tt>f</tt> mapping vertices in the graph
       to points in Cartesian 2 dimensional space (that is, a plane) such
       that for every pair of edges <tt>(u,v)</tt>,<tt>(u',v')</tt> in the 
       graph, the line segments going from <tt>f(u)</tt> to <tt>f(v)</tt>, 
       and from <tt>f(u')</tt> to <tt>f(v')</tt> do not intersect.
     */
    Planar,

    /**
       There exists a set <tt>S</tt> of vertices in the graph such that for
       every edge <tt>{u,v}</tt> in the graph, <tt>u in S iff v not in S</tt>.
     */
    Bipartite(new BipartiteChecker());

    protected UGraphChecker checker;

    UGraphProperty(UGraphChecker c)
    {
        this.checker = c;
    }

    // XXX delete me once complete.
    UGraphProperty()
    {
        this.checker = null;
    }

    /**
       Check whether or not an undirected graph satisfies the property.
     */
    public final boolean check(UGraph g)
    {
        return checker.check(g);
    }
}
