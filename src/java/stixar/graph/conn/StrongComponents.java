package stixar.graph.conn;

import stixar.graph.BasicDigraph;
import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.GraphFilter;

import stixar.graph.attr.NodeMap;
import stixar.graph.attr.ArrayNodeMap;
import stixar.graph.search.DFS;

import stixar.util.CList;

import java.util.ArrayList;
import java.util.BitSet;

/**
   Strongly Connected Components.
   <p>
   Two vertices <tt>u,v</tt> in a directed graph are said to be in the same strongly
   connected component if there is both a path from <tt>u</tt> to <tt>v</tt> and 
   a path from <tt>v</tt> to <tt>u</tt>.
   </p>
   <p>
   This class provides methods for computing the set of largest strongly connected 
   components for a given digraph.  The provided results may take the following forms.
   <ol>
   <li>A <b>component map</b> mapping every node to an integer identifying a component. </li> 
   <li>A <b>leader map</b> mapping every node to a unique representative node for its component.</li>
   <li>A <b>quotient graph</b> may be constructed, in which each component has one vertex
   and there is an edge between vertices exactly when there is an edge between nodes in the 
   respective components in the original graph.</li>
   </ol>
   </p>
   <p>
   This implementation works by way of a single depth first search in which the
   outgoing edges for each vertex are enumerated twice.  The algorithm is attributed
   to Tarjan and runs in linear time.
   </p>
 */
public class StrongComponents 
{
    protected DFS dfs;
    protected Digraph digraph;
    protected int[] components;
    protected int component;
    protected NodeMap<Node> leaders;
    protected GraphFilter filter;
    int dfsNum;
    protected ArrayList<Node> stack;

    /**
       Compute the quotient graph for an arbitrary digraph.
       <p>
       The resulting quotient graph has a node attribute associated with
       it, available via {@link #QuotientCompListMapKey}, which 
       attributes a <tt>CList&lt;Node&gt;</tt> of nodes from <tt>dg</tt>
       with every node in the quotient graph.
       </p>
       @param dg the digraph whose quotient is to be computed.
       @return an attributed quotient graph.
     */
    public static BasicDigraph quotient(Digraph dg)
    {
        return quotient(dg, null);
    }

    /**
       Compute the quotient graph for an arbitrary digraph.
       <p>
       The resulting quotient graph has a node attribute associated with
       it, available via {@link #QuotientCompListMapKey}, which 
       attributes a <tt>CList&lt;Node&gt;</tt> of nodes from <tt>dg</tt>
       with every node in the quotient graph.
       </p>
       In addition, <tt>qMap</tt> is built which attributes every
       node in <tt>dg</tt> with a node in the resulting quotient graph.
       </p>
       @param dg the digraph whose quotient is to be computed.
       @param qMap a node attribute map associating nodes in the quotient
       graph with nodes in <tt>dg</tt>.
       @return an attributed quotient graph.
     */
    public static BasicDigraph quotient(Digraph dg, NodeMap<Node> qMap)
    {
        StrongComponents scc = new StrongComponents(dg);
        scc.run();
        return scc.quotient(qMap);
    }

    /**
       Compute a leader node for every component.
       @param dg the digraph whose strong components are found
       and for each component, a leader node is selected.
       @return a NodeMap associating for every node in <tt>dg</tt>
       a leader node representing a single strongly connected component
       in <tt>dg</tt>.
     */
    public static NodeMap<Node> leaders(Digraph dg)
    {
        StrongComponents scc = new StrongComponents(dg);
        scc.run();
        return scc.leaders();
    }

    /**
       Compute a component number for every node.
       @param dg the digraph whose strong components are found
       and for each component, a unique integer is assigned.
       @return a node attribute array containing the integer id 
       of the component for each node.
     */
    public static int[] components(Digraph dg)
    {
        StrongComponents scc = new StrongComponents(dg);
        scc.run();
        return scc.components();
    }


    /**
       Construct a DFSVisitor for computing strongly connected components for the digraph
       <tt>digraph</tt>.
     */
    protected StrongComponents(Digraph digraph)
    {
        this(digraph, null, null);
    }

    protected StrongComponents(Digraph digraph, int[] comps)
    {
        this(digraph, comps, null);
    }

    protected StrongComponents(Digraph digraph, NodeMap<Node> leaders)
    {
        this(digraph, null, leaders);
    }

    protected StrongComponents(Digraph digraph, int[] comps, NodeMap<Node> leaders)
    {
        super();
        dfs = new DFS(digraph, new Visitor());
        this.digraph = digraph;
        filter = digraph.getFilter();
        reset(comps, leaders);
    }

    protected void reset(int[] comps, NodeMap<Node> leaders)
    {
        dfs.reset();
        int nnodes = digraph.nodeAttrSize();
        components = comps == null ? new int[nnodes] : comps;
        if (leaders == null)
            this.leaders = digraph.createNodeMap();
        else
            this.leaders = leaders;
        stack = new ArrayList<Node>(nnodes);
        component = 0;
        dfsNum = 0;
        filter = digraph.getFilter();
        for (int i=0; i<nnodes; ++i) {
            components[i] = Integer.MAX_VALUE;
        }
    }

    /**
       Return a representative of the component in which 
       <tt>n</tt> resides.

       @param n the node whose representative is queried.
       @return a {@link Node} representing the component.
     */
    protected final Node leader(Node n) { return n.get(leaders); }

    /**
       Return a node attribute array attributing to each node
       a leader node.
       There is exactly <tt>1</tt> leader node per component 
       in the digraph.
       @return A node attribute array attributing to each node
       a leader node.
     */
    protected final NodeMap<Node> leaders()
    {
        return leaders;
    }

    /**
       Returns a unique number identifying the component of 
       the node <tt>n</tt>

       @param n the node whose component number is queried
       @return an integer, a unique number indicating the component.
     */
    protected final int component(Node n) 
    { 
        return n.getInt(components);
    }

    /**
       Return a node attribute array identifying the component
       of each node in the digraph.

       @return a node attribute array identifying the component
       of each node in the digraph.
     */
    protected final int[] components()
    {
        return components;
    }
    
    /**
       If {@link #quotient  quotient graph is generated}, it is given an attribute
       map in which each node is associated with a list of nodes in the 
       original graph.  This attribute map can be retrieved by using 
       this key.
     */
    public static final Object QuotientCompListMapKey = new Object();


    /**
       Return the quotient graph in which each component <tt>c</tt> is linked
       to another <tt>c'</tt> if there is an edge from some node in <tt>c</tt>
       to some node in <tt>c'</tt>.
       <p>
       The returned graph has a node map associated with it which
       maps every node in the quotient graph to a CList&lt;Node&gt;
       containing all the nodes in the original graph belonging to
       the same component.  The key for this map is {@link QuotientCompListMapKey}.
       </p>
     */
    protected final BasicDigraph quotient()
    {
        NodeMap<Node> dummy = null;
        return quotient(dummy);
    }

    /**
       Return the quotient graph in which each component <tt>c</tt> is linked
       to another <tt>c'</tt> if there is an edge from some node in <tt>c</tt>
       to some node in <tt>c'</tt>.
       <p>
       The returned graph has a node map associated with it which
       maps every node in the quotient graph to a CList&lt;Node&gt;
       containing all the nodes in the original graph belonging to
       the same component. The key for this map is {@link QuotientCompListMapKey}.
       </p>
     */
    protected final BasicDigraph quotient(NodeMap<Node> qMap)
    {
        BasicDigraph quotGraph = new BasicDigraph(component, digraph.edgeSize());
        quotGraph.genNodes(component);
        NodeMap<CList<Node>> compMap = quotGraph.createNodeMap(QuotientCompListMapKey);
        BitSet m = new BitSet(component * component);
        for(Node n: digraph.nodes()) {
            if (filter != null && filter.filter(n)) continue;
            int nid = n.nodeId();
            int nComp = n.getInt(components);
            Node qnNode = quotGraph.node(nComp);
            CList<Node> compList;
            if (compMap != null) {
                if ((compList = qnNode.get(compMap)) == null) {
                    compList = new CList<Node>();
                    qnNode.set(compMap, compList);
                }
                compList.add(n);
            }
            if (qMap != null) {
                n.set(qMap, qnNode);
            }
            for (Edge e = n.out(); e != null; e = e.next()) {
                if (filter != null && filter.filter(e)) continue;
                Node t = e.target();
                int tid = t.nodeId();
                int tComp = components[tid];
                if (nComp == tComp) continue;
                if (!m.get(nComp * component + tComp)) {
                    m.set(nComp * component + tComp, true);
                    quotGraph.genEdge(qnNode, quotGraph.node(tComp));
                }
            }
        }
        return quotGraph;
    }


    protected void run()
    {
        dfs.run();
    }

    private class Visitor extends DFS.Visitor
    {
        /**
           Called when the DFS algorithm first comes accross a node <tt>n</tt>
           @see stixar.graph.search.DFS.Visitor
        */
        public final void discover(Node n)
        {
            stack.add(n);
            n.set(leaders, n);
        }
        
        /**
           Called when the DFS algorithm finishes processing a node.
           @see stixar.graph.search.DFS.Visitor
        */
        /*
          Note that we use filtering here since we do iteration
          on top of what is done in DFS.
        */
        public final void finish(Node n)
        {
            for (Edge e = n.out(); e != null; e = e.next()) {
                if (filter != null && filter.filter(e)) continue;
                Node t = e.target();
                if (t.getInt(components) == Integer.MAX_VALUE) {
                    int nn = dfs.status(n.get(leaders)).startNum;
                    int tn = dfs.status(t.get(leaders)).startNum;
                    if (nn >= tn) 
                        n.set(leaders, t.get(leaders));
                }
            }
            if (n.get(leaders) == n) {
                Node v;
                do {
                    v = stack.remove(stack.size() - 1);
                    v.setInt(components, component);
                    v.set(leaders, n);
                } while (v != n);
                component++;
            }
        }
    }
}

