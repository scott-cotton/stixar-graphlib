package stixar.graph.paths;

import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.GraphFilter;
import stixar.graph.Digraph;

import stixar.graph.attr.NativeMap;
import stixar.graph.attr.NativeNodeMap;
import stixar.graph.attr.NativeEdgeMap;
import stixar.graph.attr.IntNodeMap;
import stixar.graph.attr.FloatNodeMap;
import stixar.graph.attr.DoubleNodeMap;
import stixar.graph.attr.LongNodeMap;
import stixar.graph.attr.IntEdgeMap;
import stixar.graph.attr.FloatEdgeMap;
import stixar.graph.attr.DoubleEdgeMap;
import stixar.graph.attr.LongEdgeMap;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.ArrayNodeMap;
import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.EdgeSource;

import stixar.util.CList;
import stixar.util.ListCell;
import stixar.util.NumAdaptor;

import java.util.BitSet;
import java.util.Arrays;


/**
   Tarjan's subtree disassembly variant of the Bellman-Ford-Moore
   algorithm for generically attributed digraphs.
 */
public class BFM<T> extends BFMBase
{
    protected EdgeSource<T> weights;
    protected NodeMap<T> distMap;
    protected NumAdaptor<T> adaptor;
    

    /**
       Construct a new shortests paths finder for digraph with arbitrary edge 
       weights.

       @param g the digraph in which to find shortests paths.  This should
       be acyclic.
       @param source the vertex from which to find shortests paths.  This
       may be null, in which case shortest paths from all the DFS roots are
       computed
       @param distMap a distance map giving distances to each vertex.
       @param weights a source of edge weights.
       @param predMap a map in which to place the predecessor edges of the 
       shortests path tree for each vertex.  This may be null, in which
       case a parent map is created and may be retrieved by the {@link #parents}
       method.
       @param adaptor an adaptor for shortests paths for generic types.

       @see NumAdaptor#Int
       @see NumAdaptor#Float
       @see NumAdaptor#Long
       @see NumAdaptor#Double
     */
    public BFM(Digraph g, 
               Node source,
               EdgeSource<T> weights,
               NodeMap<T> distMap,
               NodeMap<Edge> predMap,
               NumAdaptor<T> adaptor)
    {
        super(g, source, predMap);
        this.weights = weights;
        this.distMap = distMap;
        this.adaptor = adaptor;
    }
    
    public void run()
    {
        reset();
        NodeInfo ni = source.get(niA);
        ni.cell = queue.append(ni);
        while(!queue.isEmpty()) {
            ni = queue.remove();
            if ((cycleEdge = scan(ni)) != null)
                break;
        }
    }
    

    protected  void reset()
    {
        super.reset();
        T zero = adaptor.zero();
        T inf = adaptor.inf();
        for (Node n : digraph.nodes())
            if (n == source) distMap.set(n, zero);
            else distMap.set(n, inf);
    }
    
    /*
      Scan the node associated with sInfo.
      
      If an outgoing edge connects to a another node t and 
      relaxes its distance, disassemble the subtree rooted 
      at t, and remove all the nodes in that subtree from
      the queue, since we know we have inaccurate info about their 
      distances.
      
      If the subtree contains the node s, we know we have a negative 
      cycle.  In this case, return an edge in the cycle.
    */
    protected Edge scan(NodeInfo sInfo)
    {
        Node s = sInfo.node;
        int sid = s.nodeId();
        for (Edge e = s.out(); e != null; e = e.next()) {
            if (filter != null && filter.filter(e)) {
                continue;
            }
            Node t = e.target();
            NodeInfo tInfo = t.get(niA);
            T newTDist = adaptor.add(distMap.get(s), weights.get(e));
            if (adaptor.compare(newTDist, distMap.get(t)) < 0) {
                distMap.set(t, newTDist);
                Edge nc = disassemble(sInfo, tInfo, e);
                if (nc != null)
                    return nc;
            }
        }
        return null;
    }
}


/**
   Bellman-Ford-Moore algorithm for single source shortest
   paths with arbitrary edge weights.
   <p>
   This class implements a variant of the Bellman-Ford-Moore
   algorithm for single source shortest paths with arbitrary
   edge weights.  The algorithm used is described by Tarjan
   as Subtree disassembly in a technical note from bell labs 
   from the 70s (not available online, sorry), and performs 
   "immediate" negative cycle detection.
   </p>
   <p>
   If the input graph is acyclic, {@link AcyclicSP} should
   be used.
   </p>
 */
class BFMBase 
{

    /*
      Info maintained for each node, there's a lot of it.
    */
    protected static class NodeInfo
    {
        /*
          Information stored with each node.
        */
        Node node;
        /*
          queue maintenance.
        */
        ListCell<NodeInfo> cell;
        /*
          Shortest path tree maintenance.
        */
        NodeInfo sptNext;
        NodeInfo sptPrev;
        NodeInfo sptParent;
        int sptDegree;
        

        NodeInfo(Node n)
        {
            node = n;
            sptNext = null;
            sptPrev = null;
            sptParent = null;
            sptDegree = -1;
        }
    }


    // attribute array for nod infos
    protected NodeInfo[] niA;
    // attribute array for parents in shortest path tree.
    protected NodeMap<Edge> parents;
    // the input digraph.
    protected Digraph digraph;
    // the source vertex.
    protected Node source;
    // the filters .. 
    protected GraphFilter filter;

    // a worklist queue
    protected CList<NodeInfo> queue;

    // witness edge in the negative cycle.
    protected Edge cycleEdge;

    /*
      Abstract class, only instantiable from inner classes.
     */
    protected BFMBase(Digraph dg, Node source)
    {
        this(dg, source, null);
    }

    /*
      base class, all we need except the actual edge weights
      and node distance map.
     */
    protected BFMBase(Digraph dg, 
                      Node source, 
                      NodeMap<Edge> parents)
    {
        this.digraph = dg;
        this.source = source;
        this.filter = dg.getFilter();
        this.cycleEdge = null;
        this.queue = new CList<NodeInfo>();
        if (parents == null)
            this.parents = new ArrayNodeMap<Edge>(new Edge[dg.nodeAttrSize()]);
        else
            this.parents = parents;
        this.niA = new NodeInfo[dg.nodeAttrSize()];
    }


    /**
       Return the source vertex for this single source shortest path
       problem.
     */
    public Node source()
    {
        return source;
    }
    
    /**
       Set and return the source vertex for this single source shortest
       paths problem.
     */
    public Node source(Node n)
    {
        return source = n;
    }

    /**
       Produce an attribute array of the parent edges in the shortest
       path tree.
     */
    public NodeMap<Edge> parents()
    {
        return parents;
    }

    /**
       Produce a witnessing edge in a cycle if a negative cycle is found,
       otherwise return <tt>null</tt>.
     */
    public Edge negCycleEdge()
    {
        return cycleEdge;
    }

    /**
       Return a negative cycle in the form of a path object if
       one exists from the previous run of the algorithm.
       If no negative cycle exists, return null.
     */
    public Path negCycle()
    {
        if (cycleEdge == null) {
            return null;
        }
        Node t = cycleEdge.target();
        Path path = new Path();
        path.prepend(cycleEdge);
        do {
            cycleEdge = cycleEdge.source().get(parents);
            path.prepend(cycleEdge);
        } while(cycleEdge.source() != t);
        return path;
    }

    protected Edge disassemble(NodeInfo sInfo, NodeInfo tInfo, Edge e)
    {
        Node t = tInfo.node;
        Node s = sInfo.node;
        if (tInfo.sptPrev != null) {
            NodeInfo before = tInfo.sptPrev;
            int degree = 0;
            NodeInfo nTest = null;
            for (nTest = tInfo; degree >= 0; nTest = nTest.sptNext) {
                if (nTest == sInfo) {
                    t.set(parents, e);
                    tInfo.sptParent = sInfo;
                    // neg cycle.
                    return s.get(parents);
                }
                degree += nTest.sptDegree;
                nTest.sptPrev = null;
                nTest.sptDegree = -1;
                if (nTest.cell.isValid())
                    queue.remove(nTest.cell);
            }
            before.sptNext = nTest;
            nTest.sptPrev = before;
            tInfo.sptParent.sptDegree--;
        }
        t.set(parents, e);
        tInfo.sptParent = sInfo;
        sInfo.sptDegree++;
        NodeInfo after = sInfo.sptNext;
        sInfo.sptNext = tInfo;
        tInfo.sptPrev = sInfo;
        tInfo.sptNext = after;
        after.sptPrev = tInfo;
        tInfo.cell = queue.append(tInfo);
        return null;
    }


    /*
      Abstract class reset, some more work is done
      for the particular implementations.
     */
    protected void reset()
    {
        queue.clear();
        int i=0;
        for (Node n : digraph.nodes()) {
            niA[i++] = new NodeInfo(n);
            n.set(parents, null);
        }
        NodeInfo sInfo = source.get(niA);
        sInfo.sptParent = sInfo;
        sInfo.sptNext = sInfo;
        sInfo.sptPrev = sInfo;
        this.cycleEdge = null;
    }
}

