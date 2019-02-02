package stixar.graph.conn;

import stixar.graph.UGraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.attr.IntEdgeMap;
import stixar.graph.search.DFS;

import stixar.util.CList;
import java.util.Stack;

/**
   Compute biconnected components for an undirected graph.
   <p>
   An undirected graph is <em>biconnected</em> if it remains
   connected after removing any single vertex.  Given an
   arbitrary undirected graph, we may find its biconnected components,
   or maximal subgraphs which are biconnected.  If there is
   more than one biconnected component in a connected component, then
   there are <em>articulation points</em> or vertices whose removal
   disconnects the biconnected components into connected components.
   </p>
   <p>
   Since articulation points may be connected to one or more biconnected
   components, they do not belong to a unique biconnected component.  However,
   the edges in an undirected graph do belong to a unique biconnected component. 
   </p>
   <p>
   This algorithm computes the biconnected components, giving a unique integer
   id to each component.  In addition it 
   <ol>
   <li> Computes for each edge the component to which it belongs in an 
   edge attribute array.</li>
   <li> Computes the set of articulation points</li>
   </ol>
   <p>
   The computation is performed during a single depth first search and 
   has linear time and space asymptotic complexity.  This algorithm 
   is due to Tarjan.
 */
public class BiconnectedComponents
    extends DFS.Visitor
{
    protected int[] components;
    protected int[] lowPoints;
    protected int[] dfsNums;
    protected Edge[] parents;
    protected boolean[] isArtic;
    protected DFS dfs;
    protected UGraph graph;
    protected int component;
    protected Node currentRoot;
    protected int rootChildren;
    protected CList<Node> articPoints;
    protected Stack<Edge> eStack;


    /**
       Find the articulation points of a given undirected graph.
       @param g the graph whose articulation points are to be found.
     */
    public static CList<Node> articulationNodes(UGraph g)
    {
	CList<Node> res = new CList<Node>();
	BiconnectedComponents bcc = new BiconnectedComponents(g, res);
	bcc.run();
	return bcc.articPoints;
    }

    /**
       Compute the biconnected components as an edge attribute map.
       @param g the graph whose biconnected components are to be found.
       @return an edge map indicating for each edge an integer 
       which in turn indicates the biconnected component to which
       the edge belongs.
     */
    public static IntEdgeMap getComponents(UGraph g)
    {
	BiconnectedComponents bcc = new BiconnectedComponents(g);
	bcc.run();
	return new IntEdgeMap(bcc.components);
    }

    /**
       Compute the biconnected components as an edge attribute map.
       @param g the graph whose biconnected components are to be found.
       @param artPoints a list in which to be the nodes which are
       articulation points in <tt>g</tt>.
       @return an edge map indicating for each edge an integer 
       which in turn indicates the biconnected component to which
       the edge belongs.
     */
    public static IntEdgeMap getComponents(UGraph g, CList<Node> artPoints)
    {
	artPoints.clear();
	BiconnectedComponents bcc = new BiconnectedComponents(g, artPoints);
	bcc.run();
	return new IntEdgeMap(bcc.components);
    }

    /**
       Construct a new BiconnectedComponents object for an undirected graph.
       @param g the graph whose biconnected components are to be found.
     */
    public BiconnectedComponents(UGraph g)
    {
	this(g, null);
    }

    /**
       Construct a new BiconnectedComponents object for an undirected graph.
       @param g the graph whose biconnected components are to be found.
       @param artPoints a list in which to place articulation points.
     */
    public BiconnectedComponents(UGraph g, CList<Node> artPoints)
    {
        components = new int[g.edgeAttrSize()];
        lowPoints = new int[g.nodeAttrSize()];
        parents = new Edge[g.nodeAttrSize()];
        isArtic = new boolean[g.nodeAttrSize()];
        graph = g;
        dfs = new DFS(g, new Visitor());
        component = 0;
        currentRoot = null;
        int rootChildren = 0;
        articPoints = artPoints;
        eStack = new Stack<Edge>();
    }


    /**
       Return the articulation points from the previous {@link #run}.
     */
    public CList<Node> articulationPoints()
    {
        return articPoints;
    }

    /**
       Produce an edge attribute array giving identifiers to the 
       biconnected components to which each edge belongs.

       @return an edge attribute array giving identifiers to the 
       biconnected components.
     */
    public int[] components()
    {
        return components;
    }

    /*
      Algorithm imp.
     */
    public void run()
    {
        dfs.run();
    }

    private class Visitor extends DFS.Visitor
    {
	/**
	   Overriden DFS function, internal method.
	*/
	public void treeEdge(Edge e)
	{
	    e.target().set(parents, e);
	    if (e.source() == currentRoot) {
		rootChildren++;
	    }
	    eStack.add(e);
	}
	
	/**
	   Overriden DFS function, internal method.
	*/
	public void root(Node n)
	{
	    currentRoot = n;
	    rootChildren = 0;
	}
	
	/**
	   Overriden DFS function, internal method.       
	*/
	public void backEdge(Edge e)
	{
	    Node s = e.source();
	    if (s.get(parents) != e.target()) {
		Node t = e.target();
                DFS.Status tStat = dfs.status(t);
		s.setInt(lowPoints, Math.min(s.getInt(lowPoints), 
					     tStat.startNum));
		eStack.add(e);
	    }
	}

	/**
	   Overriden DFS function, internal method.       
	*/
	public void discover(Node n)
	{
            DFS.Status nStat = dfs.status(n);
	    n.setInt(lowPoints, nStat.startNum);
	}

	/**
	   Overriden DFS function, internal method.       
	*/
	public void finish(Node n)
	{
	    Edge pEdge = n.get(parents);
	    if (pEdge == null) {
		// n is a root, if it has two or more children, it
		// is an articulation point.  Its components were
		// determined when its children were finish()'d.
		if (rootChildren >= 2 && !n.getBool(isArtic)) {
		    n.setBool(isArtic, true);
		    if (articPoints != null) articPoints.add(n);
		}
		return;
	    }
	    Node p = pEdge.source();
	    p.setInt(lowPoints, Math.min(n.getInt(lowPoints), 
					 p.getInt(lowPoints)));
	    if (n.getInt(lowPoints) >= p.getInt(lowPoints)) {
		/*
		  subtree rooted at n does not have a back edge which goes 
		  higher than p. Hence p is an articulation point and 
		  all the edges in the subtree rooted at n which aren't in some
		  other bcc are in a new bcc.  pEdge also is in this new bcc.
		*/
		Edge e = null;
		Node s = null;
		do {
		    e = eStack.pop();
		    s = e.source();
		    e.setInt(components, component);
		} while (s.getInt(dfsNums) >= n.getInt(dfsNums));
		component++;
		if (!p.getBool(isArtic)) {
		    p.setBool(isArtic, true);
		    if (articPoints != null) articPoints.add(p);
		}
	    }
	}
    }
}
