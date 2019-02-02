package stixar.graph.order;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Graph;

import stixar.graph.attr.IntNodeMap;
import stixar.graph.search.DFS;

import stixar.util.CList;
import java.util.Arrays;

/**
   Topological sorter.
   <p>
   A topological sort of a directed graph is an ordering of the 
   nodes in the graph such that if node <tt>u</tt> precedes
   nodes <tt>v</tt> in the order, then <tt>u</tt> is not
   reachable from <tt>v</tt>.
   </p>

   <p>
   Topological sorting is a useful component for producing a
   serial order which respects the constraints imposed by 
   a partial order.  As a side effect, topological sorting
   checks whether or not a graph is acyclic.  A cyclic graph
   produces an invalid sort, which can be determined by 
   the {@link TopSorter#valid} method.
   </p>
   Topological sorting is implemented as a straightforward
   application of DFS.
 */
public class TopSorter extends DFS.Visitor
    implements Algorithm, Filtering
{
    protected Graph digraph;
    protected CList<Node> tsort;
    protected int[] tnums;
    protected int tnum;
    protected boolean valid;
    protected boolean reverse;
    protected DFS dfs;

    /** 
        construct a TopSorter with the 
        number of nodes 

        @param g the digraph which is to be topologically sorted.
    */
    public TopSorter(Graph g)
    {
        digraph = g;
        int nnodes = g.nodeSize();
        tsort = new CList<Node>();
        tnums = new int[g.nodeAttrSize()];
        tnum = nnodes - 1;
        valid = true;
        reverse = false;
        dfs = new DFS(g, this);
    }

    /**
       Produce a NodeOrder for a digraph.
       @param g the graph whose nodes are to be sorted
       @return a NodeOrder for the graphs representing a topologic order.
     */
    public static NodeOrder topSortOrder(Graph g)
    {
        TopSorter tsorter = new TopSorter(g);
        tsorter.run();
        return tsorter.order();
    }

    /**
       Produce a list of nodes in topologic order.
       @param g the graph whose nodes are to be sorted.
       @return a list of the nodes in topologic order.
     */
    public static CList<Node> topSortList(Graph g)
    {
        TopSorter tsorter = new TopSorter(g);
        tsorter.run();
        return tsorter.getSort();
    }

    /**
       Produce an attribute map with topological numbers.
       @param g the graph whose topological numbering is found.
       @return an attribute map <tt>m</tt> associating a number
       with every node in <tt>g</tt> such that <tt>m(n) &lt; m(n')</tt> 
       implies that <tt>n'</tt> cannot reach <tt>n</tt> in <tt>g</tt>. 
     */
    public static IntNodeMap topSortNumbers(Graph g)
    {
	TopSorter ts = new TopSorter(g);
	ts.run();
	return new IntNodeMap(ts.tnums);
    }

    /**
       Produce a TopSort visitor for a graph with a specified 
       number of nodes.

       @param g the digraph which is to be topologically sorted.
       @param reverse whether or not to produce a reverse topological sort.
     */
    public TopSorter(Graph g, boolean reverse)
    {
        this(g);
        this.reverse = reverse;
    }

    public void run()
    {
        dfs.run();
    }

    public void reset()
    {
        Arrays.fill(tnums, -1);
        dfs.reset();
        tsort.clear();
    }

    /**
       Return a node attribute array consisting of the 
       topological number of each node.
     */
    public int[] tsNums()
    {
        return tnums;
    }

    /**
       Create a NodeOrder for the topological ordering.

       @return a NodeOrder representing the topolical ordering.
     */
    public NodeOrder order()
    {
        return new NodeOrder(digraph, tnums);
    }

    /**
       Called by {@link DFS} to identify the topological sort order.
     */
    public void finish(Node n)
    {
        if (!reverse) 
            tsort.addFirst(n);
        else
            tsort.addLast(n);
        tnums[n.nodeId()] = tnum--;
    }

    /**
       Called by {@link  DFS} to identify edges which make this graph cyclic.
     */
    public void backEdge(Edge e)
    {
        valid = false;
    }

    /** 
        return a linked list of the the nodes in topological sort order,
        or reverse topological sort order if this visitor was constructed
        with <tt>reverse=true</tt> in {@link TopSorter}.
    */
    public CList<Node> getSort() 
    { 
        return tsort; 
    }

    /** 
        return the topological number of the node n 
    */
    public int tsNum(Node n) 
    { 
        return tnums[n.nodeId()]; 
    }

    /** 
        return whether the sort is valid, ie, whether the graph
        had a cycle 
    */
    public boolean valid() 
    { 
        return valid; 
    }
}
