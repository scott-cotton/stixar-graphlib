package stixar.graph.flow;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.GraphFilter;

import stixar.graph.search.BFS;
import stixar.graph.gen.BasicDGFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;


/**
   Ford-Fulkerson MaxFlow/Min cut method, Edmonds-Clark
   algorithm.

   Note this algorithm is not a filtering algorithm.

   Make this class non-public b/c MaxFlowPR is much better.
 */
class MaxFlowEK implements Algorithm, Filtering
{

    /*
      Responsible for identifying the edges in the residual 
      subgraph.
     */
    static final class ResidualEdgeFilter implements GraphFilter
    {
        protected int[] capacity;
        protected int[] flow;

        public ResidualEdgeFilter(int[] flow,
                                  int[] cap)
        {
            this.capacity = cap;
            this.flow = flow;
        }
        
        public final boolean filter(final Edge e)
        {
            return flow[e.edgeId()] >= capacity[e.edgeId()];
        }

        public final boolean filter(final Node n)
        {
            return false;
        }
    }

    protected int[] capacity;
    protected int[] flow;
    protected Edge[] flip;

    protected Digraph digraph;
    protected Node source;
    protected Node target;
    protected BFS bfs;
    protected STVisitor vis;
    protected Set<Node> cut;
    protected Set<Node> frontier;
    protected int flowValue;


    /**
       Initialize the max flow algorithm.
       This constructor performs the necessary mechanisms for the 
       Ford-Fulkerson max flow algorithm.  In particular, it 
       makes a copy of the input graph, adds edges vu for every edge
       uv, initializes a new capacity attribute array for the new 
       graph with zero values taken for every edge except those
       in the original graph with capacities described in <tt>cap</tt>,
       initializes a flow array to all zero for all the edges, 
       and initializes a flip array, describing for every edge
       its reverse edge.
       <p>
       Due to the above initialization, the following <bf>unchecked</bf> 
       assumptions are made on the input graph.
       <ul>
       <li> The input graph is antisymmetric. </li>
       <li> The input graph is not a multigraph, ie for any pair of nodes
       <tt>u,v</tt>, there is at most one edge between them. </li>
       </ul>
       </p>
       If your graph is not a multigraph, but is partially symmetric,
       consider using another constructor.
       
       @param g the graph whose maximum flow is to be found.  We assume
       g is antisymmetric, that is for every edge from u to v, there
       does not exist an edge from v to u.
       @param s the source of the graph.
       @param t the sink in the graph.
       @param cap the edge capacities.
     */
    public MaxFlowEK(Digraph g, Node s, Node t, int[] cap)
    {
        this.flow = new int[g.edgeSize() * 2];
        this.capacity = new int[flow.length];
        this.flip = new Edge[flow.length];

        BasicDGFactory factory = new BasicDGFactory(g);

        for (Edge e : g.edges()) {
            Edge flipEdge = factory.edge(e.source().nodeId(), e.target().nodeId());
            flipEdge.set(flip, e);
            e.set(flip, flipEdge);
            int eid = e.edgeId();
            int feid = flipEdge.edgeId();
            capacity[eid] = cap[eid];
            flow[eid] = 0;
            capacity[feid] = 0;
            flow[feid] = 0;
        }
        

        this.digraph = factory.digraph();
        this.source = digraph.node(s.nodeId());
        this.target = digraph.node(t.nodeId());

        this.vis = new STVisitor(digraph.nodeSize(), s, t,
                                 flow, capacity);
        this.cut = new HashSet<Node>(digraph.nodeSize());
        this.frontier = new HashSet<Node>(digraph.nodeSize());

        this.bfs = null;
        this.flowValue = 0;
    }

    /**
       Construct a new MaxFlow algorithm.

       @param g a digraph whose flow is to be found, complete 
       with edges vu for every edge uv.
       @param s the source node.
       @param t the target node.
       @param cap the capacities.
       @param flip an attribute array for flipped edges.
     */
    public MaxFlowEK(Digraph g, Node s, Node t, int[] cap, Edge[] flip)
    {
       
        this.digraph = g;
        this.source = s;
        this.target = t;
        this.capacity = cap;
        this.flip = flip;
        this.flow = new int[capacity.length];
        this.vis = new STVisitor(digraph.nodeSize(), s, t,
                                 flow, capacity);
        this.cut = new HashSet<Node>(digraph.nodeSize());
        this.frontier = new HashSet<Node>(digraph.nodeSize());
        
        this.bfs = null;
        this.flowValue = 0;
    }

    /**
       Throws UnsupportedOperationException.
     */
    public void reset()
    {
        throw new UnsupportedOperationException();
    }

    /**
       Run the algorithm.
     */
    public void run()
    {
        digraph.addFilter(new ResidualEdgeFilter(flow, capacity));
        bfs = new BFS(digraph, vis);
        int iter = 0;
        while(true) {
            iter++;
            bfs.visit(source);
            Edge e = vis.parent(target);
            if (e == null) break;
            int aug = vis.minAugment(target);
            flowValue += aug;
            while(e != null) {
                augment(e, aug);
                e = vis.parent(e.source());
            }
            bfs.reset();
            vis.reset();
        }
        digraph.removeFilter();
    }

    /**
       Return the value of the maximum total flow through the network.
     */
    public int flowValue() { return flowValue; }


    /**
       Computes the set of nodes in the cut on the side of 
       the source which border nodes on the target side
       of the cut.
     */
    public Set<Node> frontier()
    {
        if (!frontier.isEmpty()) return frontier;
        digraph.addFilter(new ResidualEdgeFilter(flow, capacity));
        bfs.reset();
        bfs.visitor(new CutVisitor(cut));

        bfs.visit(source);
        for (Node n : cut) {
            for (Edge e = n.out(); e != null; e = e.next()) {
                Node t = e.target();
                if (capacity[e.edgeId()] == 0) continue;
                if (!cut.contains(t)) {
                    frontier.add(n);
                }
            }
        }
        digraph.removeFilter();
        return frontier;
    }

    /**
       returns the flow function.
     */
    public int[] flow() 
    {
        return flow;
    }

    protected final void augment(Edge e, int amount)
    {
        int eid = e.edgeId();
        Edge flipEdge = e.get(flip);
        flow[eid] += amount;
        flow[flipEdge.edgeId()] = -flow[eid];
    }
}

/*
  Final visitor for determining the cut set.
 */
class CutVisitor extends BFS.Visitor
{
    protected Set<Node> cut;
    
    public CutVisitor(Set<Node> cut)
    {
        super();
        this.cut = cut;
    }

    public void discover(Node n)
    {
        cut.add(n);
    }
}

/*
 */
class STVisitor extends BFS.Visitor
{
    protected Edge[] parents;
    protected int[] minAugments;
    protected int[] flow;
    protected int[] capacity;
    protected Node source;
    protected Node target;
    protected ArrayList<Node> cut;
    protected boolean done;

    public STVisitor(int nodes, Node source,
                     Node target,
                     int[] flow,
                     int[] cap)
    {
        super();
        this.flow = flow;
        this.capacity = cap;
        this.source = source;
        this.target = target;
        this.cut = new ArrayList<Node>();
        this.parents = new Edge[nodes];
        this.minAugments = new int[nodes];
        this.done = false;
    }

    public void reset()
    {
        for (int i=0; i<parents.length; ++i) {
            parents[i] = null;
            minAugments[i] = 0;
        }
        done = false;
        cut.clear();
    }

    public final boolean done() 
    { 
        return done; 
    }

    public final void discover(Node n)
    {
    }

    public final void finish(Node n)
    {
    }

    public final void treeEdge(Edge e)
    {
        Node u = e.source();
        Node v = e.target();
        parents[v.nodeId()] = e;
        int eid = e.edgeId();
        if (u == source) {
            minAugments[v.nodeId()] = capacity[eid] - flow[eid];
        } else {
            int eaug = capacity[eid] - flow[eid];
            int uaug = minAugments[u.nodeId()];
            if (uaug <= eaug) {
                minAugments[v.nodeId()] = uaug;
            } else {
                minAugments[v.nodeId()] = eaug;
            }
        }
        if (v == target) {
            done = true;
        }
    }

    public final Edge parent(Node n)
    {
        return parents[n.nodeId()];
    }

    public final int minAugment(Node n)
    {
        return minAugments[n.nodeId()];
    }
}


