package stixar.graph.flow;

import stixar.graph.Digraph;
import stixar.graph.MutableDigraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.GraphFilter;

import stixar.graph.attr.EdgeMap;
import stixar.graph.attr.IntEdgeMap;

import stixar.graph.edit.MakeSymmetric;

import stixar.util.Pair;
import stixar.util.CList;
import stixar.util.ListCell;

import java.util.List;
import java.util.BitSet;

/**
   Push-Relabel max flow and min cut algorithm with heuristics.  
   <p>For
   description of network flows, see <a href="package-summary.html">the
   package summary</a>.
   </p><p>

   <h3>Implementation Notes</h3>
   This algorithm implements the push-relabel method for computing the maximum
   flow of a directed graph.  It uses highest-level-first node selection and
   gap relabelling as well as global relabelling heuristics.  It uses a two phase
   variant of the method.  This work is derived primarily from
   <ul>
   <li> Reading the source code from 
<a href="http://www.avglab.com/andrew/soft.html">Andrew Goldberg's h_prf</a>.</li>
   <li> Reading the LEDA manual.</li>
   </ul>
   It follows neither approach to a tee, but is basically a mixture of the two.
   </p>
 */
public class MaxFlow
    implements Algorithm, Filtering
{

    /**
       Compute a min cut for a properly prepared st-digraph.
       @param digraph the input digraph.  It must be symmetric.
       @param source the source of the digraph. All incoming edges
       to the sink must have capacity <tt>0</tt>.
       @param sink the sink of the digraph.  All outgoing edges
       from the sink must have capacity <tt>0</tt>.
       @param capacities an edge attribute array describing non negative
       edge capacities.
       @param flip a set of edge attributes describing for each edge
       its reverse.
       @return a list of Nodes <tt>S</tt> from the digraph which contains the source
       and defines a min cut of the graph in terms of minimizing the sum of 
       the edges contained in <tt>S x complement(S)</tt>.
     */
    public static BitSet minCut(Digraph digraph, 
                                Node source, 
                                Node sink, 
                                IntEdgeMap capacities,
                                EdgeMap<Edge> flip)
    {
        MaxFlow mf = new MaxFlow(digraph, source, sink, capacities, flip);
        mf.run();
        return mf.minCut();
    }

    /**
       Compute a maximal flow for a properly prepared st-digraph.
       @param digraph the input digraph.  It must be symmetric.
       @param source the source of the digraph.
       @param sink the sink of the digraph.
       @param capacities an edge attribute array describing positive 
       edge capacities.
       @return an edge attribute array defining a maximum flow through the 
       graph.
     */
    public static IntEdgeMap maxFlow(Digraph digraph, 
                                     Node source, 
                                     Node sink, 
                                     IntEdgeMap capacities, 
                                     EdgeMap<Edge> flip)
    {
        MaxFlow mf = new MaxFlow(digraph, source, sink, capacities, flip);
        mf.run();
        return mf.flow();
    }


    protected Digraph digraph;
    protected Node source;
    protected Node sink;

    // flipped edge attributes.
    protected EdgeMap<Edge> flip;

    // later will generify this to all longs and maybe deal with
    // scaling floats/doubles somehow.
    protected IntEdgeMap capacities;
    protected IntEdgeMap flow;

    /*
      Attribute arrays for node and edge info.
     */
    protected NodeInfo[] nInfoA;
    protected EdgeInfo[] eInfoA;
    /*
      Each bucket contains a list of active and inactive nodes
      which are labelled with the number corresponding to the
      bucket index in this array.
     */
    protected Bucket[] buckets;

    protected float h;
    protected int work;

    /*
      We keep a bound on the range of buckets to help speed up
      selection of nodes for push/relabel.
     */
    protected int maxBucket;
    protected int minBucket;
    /*
      Two phase approach.
     */
    protected int phase;
    /**
       Statistics about a run of the algorithm.
     */
    public static class Statistics
    {
        /** total number of relabels.*/
        public int ttlRelabels;
        /** total number of non saturating pushes*/
        public int ttlNonSatPush;
        /** total number of saturating pushes */
        public int ttlSatPush;
        /** total number of gap heuristic relabels */
        public int ttlGap;
        /** total number of global relabellings */
        public int ttlGlobal;
        /** start time milliseconds */
        public long startTime;
        /** stop time milliseconds */
        public long stopTime;
        public long phaseTime;
        public int numNodes;
        public int numEdges;

        public String toString()
        {
            return String.format
                ("%-15s: %d(%d,%d)\n%-15s: %d\n%-15s: %d\n%-15s: %d\n%-15s: %d\n%-15s: %d\n%-15s: %d\n%-15s: %d",
                 "Time", (stopTime - startTime), phaseTime - startTime, stopTime - phaseTime,
                 "Nodes", numNodes, "Edges", numEdges, "Relabels", ttlRelabels, 
                 "Push (NonSat)", ttlNonSatPush, 
                 "Push (Sat)", ttlSatPush, "Gap", ttlGap, "Global", ttlGlobal);
        }
    }

    protected Statistics stats;

    

    /**
       Construct a new MaxFlow push-relabel algorithm.  The input
       should be adequately prepared for a maximum flow.  In particular,
       <ul>
       <li>The digraph should be symmetric, so that for every edge <tt>u,v</tt>
       there is an edge <tt>v,u</tt>.</li>
       <li>The capacities should be non-negative</li>
       <li>The sink vertex should contain no outgoing edges with non zero capacity.</li>
       <li>The source vertex should contain no incoming edges with non zero capacity.</li>
       </ul>

       For preparing a graph for this method, please refer to the
       {@link MakeSymmetric} symmetrizer.
       
       @param dg A digraph as described above.
       @param source The source vertex.
       @param sink The sink vertex.
       @param caps The capacities of the edges.  If an edge was added
       just for symmetrization, its capacity should be <tt>0</tt>.
       @param flip An attribute array containing the reverse of each edge
       for each edge.
     */
    public MaxFlow(Digraph dg, Node source, Node sink, IntEdgeMap caps, EdgeMap<Edge> flip)
    {
        this.digraph = dg;
        this.source = source;
        this.sink = sink;
        this.nInfoA = new NodeInfo[dg.nodeAttrSize()];
        this.eInfoA = new EdgeInfo[dg.edgeAttrSize()];
        this.buckets = new Bucket[dg.nodeSize() * 2];
        this.flow = null;
        this.flip = flip;
        this.capacities = caps;
        this.h = 5f;
        this.work = 0;

    }

    /**
       Return the maximum flow for the given digraph.
     */
    public IntEdgeMap flow()
    {
        if (flow != null) 
            return flow;
        flow = digraph.createIntEdgeMap();
        for (Edge e : digraph.edges()) {
            EdgeInfo ei = e.get(eInfoA);
            int eid = e.edgeId();
            int ecap = e.getInt(capacities);
            if (ecap > 0)
                flow.set(e, ecap - ei.residCap);
        }
        return flow;
    }

    /**
       Return the statistics from the previous run.
     */
    public Statistics statistics()
    {
        return stats;
    }

    /**
       Returns the global relabelling factor, which controls how frequently 
       global relabelling occurs.
       <p>
       If this method returns <tt>h</tt>, then global relabelling occurs
       every <tt>edgeSize()/h</tt> relabelling operations.  Global
       relabelling is built on breadth first search and takes <tt>O(edgeSize())</tt>
       time. The default value is <tt>5</tt>.
       </p>
       @return the global relabelling factor.
     */
    public float globalFactor()
    {
        return h;
    }

    /**
       Set and returns the global relabelling factor, which controls how frequently 
       global relabelling occurs.
       <p>
       If this method returns <tt>h</tt>, then global relabelling occurs
       every <tt>edgeSize()/h</tt> relabelling operations.  Global
       relabelling is built on breadth first search and consequently 
       takes <tt>O(edgeSize())</tt> time.
       </p>
       @return the global relabelling factor.
     */
    public float globalFactor(float h)
    {
        return this.h = h;
    }

    /**
       Return the total amount of flow which can go from the source
       to the sink in the network.
     */
    public int totalFlow()
    {
        NodeInfo ni = sink.get(nInfoA);
        return ni.excess;
    }

    /**
       For use after {@link #run}ning the algorithm, this method computes a mincut.

       @return a list of the nodes which comprise a cut of the graph and contains
       the source vertex.  The list is in breadth first search order. The method
       may also return <tt>null</tt> if the computed flow is not optimal, but
       this should not happen.
     */
    public BitSet minCut()
    {
        CList<NodeInfo> queue = new CList<NodeInfo>();
        BitSet result = new BitSet(digraph.nodeAttrSize());
        for (NodeInfo ni : nInfoA)
            ni.globalDone = false;
        NodeInfo sInfo = source.get(nInfoA);
        queue.addFirst(sInfo);
        sInfo.globalDone = true;
        while(!queue.isEmpty()) {
            NodeInfo ui = queue.removeFirst();
            ui.node.in(result, true);
            if (ui.node == sink) {
                return null;
            }
            for (Edge e = ui.node.out(); e != null; e = e.next()) {
                EdgeInfo ei = e.get(eInfoA);
                if (e.getInt(capacities) - ei.residCap > 0) {
                    NodeInfo vi = ei.edge.target().get(nInfoA);
                    if (!vi.globalDone) {
                        queue.append(vi);
                    }
                }
            }
        }
        return result;
    }

    /**
       Re-initialize the data structures in order to re-run the algorithm.
     */
    protected void reset()
    {
        this.phase = 1;
        stats = new Statistics();
        stats.numNodes = digraph.nodeSize();
        stats.numEdges = digraph.edgeSize();
        initInfo();
        pushSource();
        initBuckets();
        lowerGlobal();
    }

    /**
       Compute the maximum flow.
     */
    public void run()
    {
        reset();
        NodeInfo uInfo;
        stats.startTime = System.currentTimeMillis();
        for (;;) {
            uInfo = nextNode();
            if (uInfo == null) {
                if (phase == 2) break;
                initPhase2();
                continue;
            }
            if (!discharge(uInfo))
                relabel(uInfo);
        }
        stats.stopTime = System.currentTimeMillis();
    }

    protected void initPhase2()
    {
        stats.phaseTime = System.currentTimeMillis();
        int topBuck = buckets.length - 1;
        for (NodeInfo ni : nInfoA) {
            if (ni.node == source || ni.node == sink)
                continue;
            if (ni.excess > 0) {
                buckets[ni.label].active.remove(ni.cell);
                ni.cell = buckets[topBuck].active.append(ni);
            } else {
                buckets[ni.label].inActive.remove(ni.cell);
                ni.cell = buckets[topBuck].inActive.append(ni);
            }
            ni.label = topBuck;
        }
        phase = 2;
        lowerGlobal();
        // now all nodes reaching sink in resid subgraph are inactive.
        upperGlobal();
        
        maxBucket = topBuck;
        minBucket = buckets.length / 2;
    }

    /**
       A checker which tests that the result is an optimal flow.
     */
    public boolean check()
    {
        boolean result = true;
        for (Node n : digraph.nodes()) {
            NodeInfo ni = n.get(nInfoA);
            if (ni.node != source && ni.node != sink && ni.excess != 0) {
                result = false;
                System.err.println("node " + ni.node + " excess " + ni.excess + " label " + ni.label);
            }
        }
        int nsz = digraph.nodeSize();
        for (int i=0; i<2 * nsz; ++i) {
            Bucket b = buckets[i];
            if (!b.active.isEmpty()) {
                System.out.println("bucket " + i + " has " + b.active.size() + " members.");
            }
        }
        if (flow == null)
            flow = digraph.createIntEdgeMap();

        for (Edge e : digraph.edges()) {
            EdgeInfo ei = e.get(eInfoA);
            int cap = e.getInt(capacities);
            EdgeInfo rev = ei.reverse;
            if (cap > 0) {

                int f = cap - ei.residCap;
                flow.set(e, f);
                if (f < 0) {

                    result = false;
                    System.err.println("edge " + ei.edge + " " + (cap - ei.residCap) + "/" + cap);
                }
            }
        }
        // check optimality
        if (result == true)
            result &= (minCut() != null);
        return result;
    }

    /*
      Find the highest labelled active node.
     */
    protected NodeInfo nextNode()
    {
        for  (int i=maxBucket; i>= minBucket; i--) {
            Bucket bucket = buckets[i];
            if (!bucket.active.isEmpty())
                return bucket.active.getFirst();
            if (bucket.inActive.isEmpty())
                maxBucket = i;
        }
        return null;
    }

    /*
      Find the least label which will make uInfo active.
      move uInfo from its current bucket to its new bucket.
      set the cur outgoing edge to point to the one with
      target with the minimum label.  Perform gap relabelling
      if possible.
    */
    protected void relabel(NodeInfo uInfo)
    {
        stats.ttlRelabels++;
        assert uInfo != null;
        int newLabel = buckets.length - 1;
        Bucket obuck = buckets[uInfo.label];
        int olabel = uInfo.label;
        obuck.active.remove(uInfo.cell);
        
        uInfo.curEdge = null;
        for (Edge e = uInfo.node.out(); e != null; e = e.next()) {
            EdgeInfo eInfo = e.get(eInfoA);
            if (eInfo.residCap > 0) {
                NodeInfo vInfo = e.target().get(nInfoA);
                if (vInfo.label+1 < newLabel) {
                    newLabel = vInfo.label + 1;
                    uInfo.curEdge = e;
                }
            }
        }
        //System.out.println("new label for " + uInfo.node + ": " + olabel + " -> "+ newLabel);
        // buckets, etc.
        Bucket nbuck = buckets[newLabel];
        uInfo.cell = nbuck.active.append(uInfo);
        uInfo.label = newLabel;
        if (uInfo.label > maxBucket)
            maxBucket = uInfo.label;
        if (phase == 1 && obuck.active.isEmpty() && obuck.inActive.isEmpty()&& olabel < buckets.length / 2)
            gap(olabel);
        if (h * ++work > digraph.edgeSize())
            global();
    }

    /*
      Attempt to discharge all the excess from a given node.
      Return whether or not the node should be relabelled.

      PRE: uInfo.excess > 0, uInfo is in the active list of 
      buckets[uInfo.label]

      POST: uInfo.excess == 0 if there exists edges
      over which to push all the excess.  If this is
      the case, uInfo is in the inactive list of buckets[uInfo.label].

      RETURN: whether or not a eligible outgoing edge was found,
      or equivalently, whether or not the excess was reduced.
     */
    protected boolean discharge(NodeInfo uInfo)
    {
        NodeInfo vInfo;
        EdgeInfo eInfo;
        boolean eligibleFound = true;
        while(uInfo.excess > 0 && eligibleFound) {
            eligibleFound = false;
            Edge e;
            for (e = uInfo.curEdge; e != null; e = e.next()) {
                //System.out.print("examinging " + e + " ");
                eInfo = e.get(eInfoA);
                if (eInfo.residCap <= 0) {
                    //vInfo = e.target().get(nInfoA);
                    //System.out.println("resid Cap zero: " + eInfo.residCap + " vInfo.label was " + vInfo.label);
                    continue;
                }
                vInfo = e.target().get(nInfoA);
                if (vInfo.label < uInfo.label) {
                    int delta = Math.min(uInfo.excess, eInfo.residCap);
                    uInfo.excess -= delta;
                    if (vInfo.excess == 0 && vInfo.node != source && vInfo.node != sink) {
                        Bucket b = buckets[vInfo.label];
                        b.inActive.remove(vInfo.cell);
                        vInfo.cell = b.active.append(vInfo);
                    }
                    vInfo.excess += delta;
                    eInfo.residCap -= delta;
                    eInfo.reverse.residCap += delta;
                    eligibleFound = true;
                    if (uInfo.excess == 0) {
                        stats.ttlSatPush++;
                    } else {
                        stats.ttlNonSatPush++;
                    }
                    break;
                }  // if label
            } // for edge
            uInfo.curEdge = e == null ? null : e.next();
        }
        if (uInfo.excess == 0) {
            Bucket b = buckets[uInfo.label];
            b.active.remove(uInfo.cell);
            uInfo.cell = b.inActive.append(uInfo);
        }
        // note that if eligibleFound is false, the node must have excess
        // and hence cannot be active, since we assume the node is active
        // on input.
        return eligibleFound;
    }

    /*
      "Gap" heuristic.  This method is called with a level m < digraph.nodeSize()
      when the level m becomes empty.  At this point in time, there is no path
      from any nodes in a level l with m < l < n to the sink in the residual
      graph.  Consequently, no flow can be sent from these nodes.  So we
      go ahead and relabel them to level n.
     */
    protected void gap(int m)
    {
        int nsz = buckets.length / 2;
        Bucket d = buckets[nsz];
        for (int i=m; i<nsz; ++i) {
            Bucket b = buckets[i];
            while(!b.active.isEmpty()) {
                NodeInfo ni = b.active.poll();
                ni.cell = d.active.append(ni);
                ni.label = nsz;
                ni.curEdge = ni.node.out();
                stats.ttlGap++;
            }
            while(!b.inActive.isEmpty()) {
                NodeInfo ni = b.inActive.poll();
                ni.cell = d.inActive.append(ni);
                ni.label = nsz;
                ni.curEdge = ni.node.out();
                stats.ttlGap++;
            }
        }
        if (maxBucket < nsz)
            maxBucket = nsz;
    }

    /*
      Global heuristic relabeling.
      
      During phase 1: relabel nodes which can reach sink in residual subgraph.
      then relabel nodes which can reach source in the reverse residual subgraph.

      During phase 2: only relabel nodes which can reach source.

      POST: minBucket and maxBucket are set.
     */
    protected void global()
    {
        int topBuck = buckets.length - 1;
        int nsz = digraph.nodeSize();
        for (NodeInfo ni : nInfoA) {
            ni.globalDone = false;
            if (ni.node == source || ni.node == sink) {
                continue;
            }
            if (phase == 2 && ni.label < nsz)
                continue;
            if (ni.excess > 0) {
                buckets[ni.label].active.remove(ni.cell);
                ni.cell = buckets[topBuck].active.append(ni);
            } else {
                buckets[ni.label].inActive.remove(ni.cell);
                ni.cell = buckets[topBuck].inActive.append(ni);
            }
            ni.label = topBuck;
        }
        if (phase == 1)
            lowerGlobal();
        upperGlobal();
        if (phase == 1) {
            maxBucket = buckets.length / 2  - 1;
            minBucket = 0;
        } else {
            maxBucket = topBuck;
            minBucket = buckets.length / 2;
        }
        stats.ttlGlobal++;
        work = 0;
    }

    /*
      Compute distances to sink in residual graph.
      by backwards bfs.

      PRE: all nodes have a label >= digraph.nodeSize(), the phase
      is either 1 or just set to 2 for the purposes of initializing
      phase 2.

      POST: all nodes which can reach the sink in the residual subgraph
      are labelled with the shortest path (in terms of number of edges)
      distances to the sink, and put in the appropriate bucket, which has
      value < digraph.nodeSize().  All nodes which are relabelled have
      the attribute globalDone set to true.

      If during the first phase, then these reachable nodes are classified
      as active or not depending as usual on the value of their excesses.

      If during the second phase, then these nodes are classified as inactive.
      
     */
    protected void lowerGlobal()
    {
        CList<NodeInfo> queue = new CList<NodeInfo>();
        queue.addFirst(sink.get(nInfoA));
        int nSize = buckets.length / 2;
        // sink label is 0.
        while(!queue.isEmpty()) {
            NodeInfo ni = queue.removeFirst();
            ni.globalDone = true;
            for (Edge e = ni.node.out(); e != null; e = e.next()) {
                EdgeInfo eInfo = e.get(eInfoA);
                Edge rev = e.get(flip);
                EdgeInfo revInfo = rev.get(eInfoA);
                // check whether the target of e can reach the source
                // in resid graph.
                if (revInfo.residCap == 0)
                    continue;
                Node u = e.target();
                NodeInfo uInfo = u.get(nInfoA);
                if (uInfo.label >= nSize && u != source) {
                    int newLabel = ni.label + 1;
                    Bucket b = buckets[uInfo.label];
                    Bucket d = buckets[newLabel];
                    if (phase == 1) {
                        if (uInfo.excess == 0) {
                            b.inActive.remove(uInfo.cell);
                            uInfo.cell = d.inActive.append(uInfo);
                        } else {
                            b.active.remove(uInfo.cell);
                            uInfo.cell = d.active.append(uInfo);
                        }
                        uInfo.label = newLabel;
                        uInfo.curEdge = uInfo.node.out();
                        queue.append(uInfo);
                    } else { // phase 2
                        if (uInfo.excess == 0) {
                            b.inActive.remove(uInfo.cell);
                        } else {
                            b.active.remove(uInfo.cell);
                        }
                        uInfo.cell = d.inActive.append(uInfo);
                        uInfo.label = newLabel;
                        uInfo.curEdge = uInfo.node.out();
                        queue.append(uInfo);
                    }
                }
            }
        }
    }

    /*
      Compute distances to source in reverse residual graph.
      An edge (u,v) is in the reverse residual graph if
      its reverse has a positive flow 

      PRE: all nodes which can reach the sink in the residual subgraph
      have globalDone true, and a label < digraph.nodeSize(). All other
      nodes have a label == 2*digraph.nodeSize() - 1

      POST:  Every node which can reach the source via an edges with
      positive flow is relabelled to n + d(n) where d(n) is the 
      length of the shortest path (in terms of # of edges) from n 
      to the source.  Each such node has label globalDone == true
      and is placed in the appropriate bucket with the appropriate
      active/inactive distinction.
     */
    protected void upperGlobal()
    {
        CList<NodeInfo> queue = new CList<NodeInfo>();
        queue.addFirst(source.get(nInfoA));
        int top = buckets.length - 1;
        // source label is digraph.nodeSize()
        while(!queue.isEmpty()) {
            NodeInfo ni = queue.removeFirst();
            ni.globalDone = true;
            for (Edge e = ni.node.out(); e != null; e = e.next()) {
                EdgeInfo eInfo = e.get(eInfoA);
                int eCap = e.getInt(capacities);
                if (eInfo.residCap < eCap)
                    continue;
                Node u = e.target();
                if (u == source || u == sink) continue;
                NodeInfo uInfo = u.get(nInfoA);
                if (uInfo.label  == top) {
                    int newLabel = ni.label + 1;
                    Bucket b = buckets[uInfo.label];
                    Bucket d = buckets[newLabel];
                    if (uInfo.excess == 0) {
                        b.inActive.remove(uInfo.cell);
                        uInfo.cell = d.inActive.append(uInfo);
                    } else {
                        b.active.remove(uInfo.cell);
                        uInfo.cell = d.active.append(uInfo);
                    }
                    uInfo.label = newLabel;
                    uInfo.curEdge = uInfo.node.out();
                    queue.append(uInfo);
                }
            }
        }
    }

    /*
      Initialize the NodeInfo and EdgeInfo objects.
      For each NodeInfo, we set its current edge,
      label, excess, and corresponding node.
      labels are initialized to digraph.nodeSize() except
      for the sink (they are subsequently updated with
      a pushSource(); lowerGlobal();)

      The EdgeInfo edge, reverse, and residual capacities 
      are set.  Initially, the residual capacities equal
      the capacities.

      A check is also performed for a non-negative capacities in
      the process.
     */
    protected void initInfo()
    {
        for (Node n : digraph.nodes()) {
            NodeInfo nInfo = new NodeInfo();
            n.set(nInfoA, nInfo);
            nInfo.cell = null;
            nInfo.curEdge = n.out();
            if (n == sink) {
                nInfo.label = 0;
            } else {
                nInfo.label = buckets.length / 2;
            }
            nInfo.excess = 0;
            nInfo.node = n;
            for (Edge e = n.out(); e != null; e = e.next()) {
                EdgeInfo eInfo = new EdgeInfo();
                e.set(eInfoA, eInfo);
                eInfo.edge = e;
                eInfo.reverse = e.get(flip).get(eInfoA);
                if (eInfo.reverse != null)
                    eInfo.reverse.reverse = eInfo;
                eInfo.residCap = e.getInt(capacities);
                if (eInfo.residCap < 0) {
                    throw new IllegalArgumentException
                        ("bad negative capacity.");
                }

            }
        }
    }

    /*
      Takes every non-s-t-node and puts in in an active or inactive
      bucket at level digraph.nodeSize().  Set the maxBucket and
      minBucket attributes.

      PRE: called in sequence after initInfo(); pushSource();
     */
    protected void initBuckets()
    {
        for (int i=0; i<buckets.length; ++i) {
            buckets[i] = new Bucket();
        }
        int nsz = digraph.nodeSize();
        Bucket buck = buckets[nsz];
        for (Node n : digraph.nodes()) {
            if (n == source || n == sink) 
                continue;
            NodeInfo nInfo = n.get(nInfoA);
            if (nInfo.excess > 0) {
                //System.out.println("adding a " + nInfo.node + " w/ excess " + nInfo.excess);
                nInfo.cell = buck.active.append(nInfo);
            } else {
                //System.out.println("adding ia " + nInfo.node + " w/ excess " + nInfo.excess);
                nInfo.cell = buck.inActive.append(nInfo);
            }
        }
        maxBucket = nsz;
        minBucket = 0;
    }

    /*
      Push out all the flow over every edge leading from the source.
      This is used in initialization after initInfo(); and before
      initBuckets().
     */
    protected void pushSource()
    {
        for (Edge e = source.out(); e != null; e = e.next()) {
            EdgeInfo eInfo = e.get(eInfoA);
            NodeInfo uInfo = e.source().get(nInfoA);
            NodeInfo vInfo = e.target().get(nInfoA);
            int delta = eInfo.residCap;
            uInfo.excess -= delta;
            vInfo.excess += delta;
            eInfo.residCap -= delta;
            eInfo.reverse.residCap += delta;
        }
    }


    /*
      We store all the info about a node in one object
      in hopes this will be faster than a set of attribute arrays.
     */
    static final class NodeInfo
    {
        Node node;
        int label; // label(sink) = 0, label(source) = n, always stays within 2n.
        int excess;  // sum of incoming flow - sum of outgoing flow.
        Edge curEdge; // invariant: every edge b4 curEdge is ineligable
        ListCell<NodeInfo> cell;
        // whether or not the global update has been done
        // also abused for bfs state in minCut().
        boolean globalDone; 
    }

    /*
      Maintain reverse edge pointers and flow and residual capacity.
     */
    protected static final class EdgeInfo
    {
        Edge edge;
        EdgeInfo reverse;
        int residCap; // invariant: edge.getInt(capacities) - residCap == flow
    }

    /*
      A bucket maintains the active and inactive nodes with
      a given label.
    */
    protected static final class Bucket
    {
        CList<NodeInfo> active;
        CList<NodeInfo> inActive;
        
        Bucket()
        {
            active = new CList<NodeInfo>();
            inActive = new CList<NodeInfo>();
        }
    }
}
