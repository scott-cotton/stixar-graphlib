package stixar.graph.paths;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Digraph;

import stixar.graph.attr.NodeMap;
import stixar.graph.attr.ArrayNodeMap;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.EdgeSource;

import stixar.graph.GraphFilter;

import stixar.graph.search.DFS;

import stixar.util.CList;
import stixar.util.NumAdaptor;

import java.util.Arrays;


/**
   Longests paths in acyclic digraphs with generic attributes.

   <p>
   The longests path in an acyclic graph is determined in linear time
   and space by exploiting a topological sort of the graph.  
   </p>
   <p>
   Longests paths are called <em>critical paths</em> in the context of
   scheduling and task graph problems.  This algorithm may be used to compute
   schedules for a set of tasks which have precedence relation.
   </p>

 */
public class AcyclicLP<T> extends AcyclicLPBase
{
    protected NumAdaptor<T> adaptor;
    protected NodeMap<T> distMap;
    protected EdgeSource<T> weights;
    
    /**
       Construct a new longest paths finder.

       @param dg the digraph in which to find longests paths.  This should
       be acyclic.
       @param source the vertex from which to find longests paths.  This
       may be <tt>null</tt>, in which case longest paths from all the DFS roots are
       computed.
       @param target the desired target vertex.  This can be <tt>null</tt>, in which
       case longests paths to all reachable vertex from source are computed.
       @param dMap a distance map giving distances to each vertex.
       @param wMap a source of edge weights.
       @param pMap a map in which to place the predecessor edges of the 
       longests path tree for each vertex.  This may be <tt>null</tt>, in which
       case a parent map is created and may be retrieved by the {@link #parents}
       method.
       @param adaptor an adaptor for longests paths for generic types.

       @see NumAdaptor#Int
       @see NumAdaptor#Float
       @see NumAdaptor#Long
       @see NumAdaptor#Double
     */
    public AcyclicLP(Digraph dg, 
                     Node source, 
                     Node target, 
                     NodeMap<T> dMap, 
                     EdgeSource<T> wMap,
                     NodeMap<Edge> pMap,
                     NumAdaptor<T> adaptor)
    {
        super(dg, source, target, pMap);
        this.adaptor = adaptor;
        this.distMap = dMap;
        this.weights = wMap;
    }
    
    protected void reset()
    {
        super.reset();
        T zero = adaptor.zero();
        T minusInf = adaptor.minusInf();
        for (Node n : digraph.nodes()) {
            if (n == source) {
                distMap.set(n, zero);
            } else {
                distMap.set(n, minusInf);
            }
        }
    }
    
    public void run()
    {
        reset();
        /*
          Get top sort, possibly rooted at source.
        */
        if (source != null)
            dfs.run();
        else
            dfs.visit(source);
        /*
          Go through topologically and relax edges.
        */
        T minInf = adaptor.minusInf();
        for (Node n : tsortList) {
            if (filter != null && filter.filter(n)) continue;
            if (n == target) break;
            T nDist = distMap.get(n);
            if (nDist == minInf) continue;
            for (Edge e = n.out(); e != null; e = e.next()) {
                if (filter != null && filter.filter(e)) continue;
                T weight = weights.get(e);
                T newDist = adaptor.add(nDist, weight);
                Node t = e.target();
                T tDist = distMap.get(t);
                if (adaptor.compare(newDist, tDist) > 0) {
                    distMap.set(t, newDist);
                    t.set(parents, e);
                }
            }
        }
    }
}




/**
   Single Source Longests Paths for acyclic graphs.
 */
class AcyclicLPBase extends DFS.Visitor
{
    protected Digraph digraph;
    protected Node source;
    protected Node target;
    protected DFS dfs;
    protected NodeMap<Edge> parents;
    protected CList<Node> tsortList;
    protected GraphFilter filter;

    /*
      Instantiate everything but the weights and distances.
     */
    protected AcyclicLPBase(Digraph dg, 
                            Node source, 
                            Node target,
                            NodeMap<Edge> predMap)
    {
        this.source = source;
        this.target = target;
        if (predMap == null) {
            this.parents = new ArrayNodeMap<Edge>(new Edge[dg.nodeAttrSize()]);
        } else {
            this.parents = predMap;
        }
        this.dfs = new DFS(dg, this);
        this.tsortList = new CList<Node>();
        this.digraph = dg;
        this.filter = dg.getFilter();
    }



    /**
       Return an attribute array for the nodes indicating their parent
       edges in the longest path tree.
     */
    public NodeMap<Edge> parents()
    {
        return parents;
    }

    protected void reset()
    {
        dfs.reset();
        tsortList.clear();
        for (Node n : digraph.nodes()) {
            n.set(parents, null);
        }
    }

    public final void finish(Node n)
    {
        tsortList.addFirst(n);
    }

    public final void backEdge(Edge e)
    {
        throw new IllegalStateException
            (String.format("cyclic graph used in AcyclicLP. offending edge: %s", e));
    }
}
