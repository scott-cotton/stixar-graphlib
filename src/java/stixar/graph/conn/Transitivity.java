package stixar.graph.conn;

import stixar.graph.MutableDigraph;
import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Algorithm;
import stixar.graph.Filtering;
import stixar.graph.BasicDigraph;

import stixar.graph.attr.ByteNodeMatrix;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.NodeMatrix;
import stixar.graph.order.TopSorter;
import stixar.graph.order.NodeOrder;
import stixar.util.CList;

import java.util.PriorityQueue;
import java.util.ArrayList;

/**
   Transitivity Algorithms.
   <p>
   The transitive closure of a directed graph <tt>G=(V,E)</tt> is
   a directed graph <tt>G=(V,E<sup>*</sup>)</tt> where <tt>E<sup>*</sup></tt> contains
   all the edges in <tt>E</tt> and in addition has the following transitivity
   property:
   <center>
   <tt>(u,v) in E<sup>*</sup> and (v,w) in E<sup>*</sup> implies (v,w) in E<sup>*</sup></tt>
   </center>
   </p>
   <p>
   Transitive closure is basically a cubic time operation (<tt>O(|V||E|)</tt>) and 
   <tt>E<sup>*</sup></tt> 
   can
   contain up to quadratic space.  For these reasons, can be difficult to compute
   transitive closure for large graphs.  
   </p>
   <p>
   For the case of an arbitrary digraph, this algorithm works as follows.
   First, {@link StrongComponents strong components} are computed.  Then the
   {@link StrongComponents#quotient quotient graph} is constructed, which is
   acyclic.  The quotient graph is then {@link stixar.graph.order.TopSorter
   topologically sorted}, and in reverse topological order, each node's
   reachable set is computed as the union of the reachable sets in all its
   outgoing edges.  The process of unioning and storing the reachable sets can
   be much more costly than the rest of the algorith, which is linear.
   Acyclic graphs are treated similarly, but the strong components computation
   is skipped.
   <p>
   
   The <em>transitive reduction </em> of a directed graph <tt>G=(V,E)</tt> is a
   graph <tt>G<sup>red</sup>=(V,E<sup>red</sup>)</tt> with a minimal number of
   edges whose transitive closure is equal to the transitive closure of <tt>g</tt>.
   If <tt>G</tt> is acyclic, then <tt>E<sup>red</sup></tt> is a subset of 
   <tt>E</tt> and is uniquely defined.  If <tt>G</tt> is not acyclic, then 
   these properties do not necessarily hold.  Moreover, if <tt>G</tt> is not
   acyclic, then finding a smallest subset of edges <tt>E<sup>-</sup></tt> from <tt>G</tt> 
   such that the transitive closure of <tt>(V,E<sup>-</sup>)</tt>) equals the transitive
   closure of <tt>G</tt> is NP-complete.
   </p>
   Hence we supply methods for computing the transitive closure and reduction.
   of acyclic and arbitrary digraphs.
   <p>

   <p>
   In addition, we supply a method for calculating a {@link #compactClosure compact
   representation of the transitive closure of a digraph} which allows for
   storing the transitive closure in a structure which is likely to be much
   smaller than quadratic.
   </p>
   
   The transitive closure algorithm, which is also used in the transtive
   reduction computations, is inspired by <a
   href="http://www.cs.hut.fi/~enu/thesis.html">the thesis of Nuutila</a>, but
   implements a simpler multi-pass method.
   </p>
**/
   /*
   Reachable sets are represented by sorted lists of integer
   ranges, for example, the set {1,2,3,5,6,8} is represented
   as [1,4), [5,7), [8,9).  Each integer represents a nodeThe set is sorted by the lower bound of 
   each range, and the invariants that each range is <em>disjoint</em>,
   <em>discontiguous</em>, and <em>non-empty</em> are maintained. 
   </p><p>
   When a node in the quotient graph <tt>u</tt> is processed, each of its 
   successors has a sorted list of these ranges.  A priority  
   queue is created containing the least range from each child.
   whenever a range is popped from the queue, it is checked against
   a current range for the current node.  If the two ranges are mergeable,
   they are merged into one range, otherwise, the current range is 
   appended to the the current node's list of ranges, and a new empty
   range is created for the current node's range.
   </p><p>
   Reachability from s to t in the original graph then proceeds by finding the 
   the components of s and t and checking if they are equal or if in the 
   quotient graph the component of t is reachable from the component of s.
   Reachability in the quotient graph is in turn computed by a simple inclusion
   in a set of intervals, which is a <tt>log(n)</tt> operation for 
   <tt>n</tt> the number of intervals after merging.
   </p>
   This method is inspired by <a href="http://www.cs.hut.fi/~enu/thesis.html">the thesis of 
   Nuutila</a>, but implements a simpler multi-pass method.
 */
public class Transitivity 
{
    protected IRange[] rangeLists;
    protected IRange[] ranges;
    protected int[] start;
    protected int[] end;
    protected int numMerges;
    protected int numUnions;

    protected Digraph digraph;
    protected Digraph quotient;
    protected StrongComponents scc;
    protected TopSorter tsorter;
    protected PriorityQueue<IRange> pq;

    protected Transitivity(Digraph digraph)
    {
        this.digraph = digraph;
    }


    /**
       Compute a reachability matrix for the digraph.
       <p>
       A reachability matrix allows for constant time reachability querying
       at the expense of the space used to store the matrix.
       If the graph is large or the number of reachability queries
       does not exceed <tt>|V|<sup>2</sup> / log(|V|)</tt>, then using the 
       {@link #compactClosure} method is recomended.
       </p>
       @param dg a digraph.
       @return a matrix whose entries <tt>(i,j)</tt> are <tt>1</tt> if 
       <tt>i</tt> can reach <tt>j</tt> in the graph <tt>dg</tt>, and <tt>0</tt>
       otherwise.
     */
    public static ByteNodeMatrix closure(Digraph dg)
    {
        Transitivity t = new Transitivity(dg);
        t.run();
        ByteNodeMatrix m = dg.createByteNodeMatrix();
        for (Node i : dg.nodes()) {
            for (Node j : dg.nodes()) {
                if (t.reaches(i, j))
                    m.set(i, j, (byte) 1);
            }
        }
        return m;
    }

    /**
       Produce a compact representation of the transitive closure
       of an arbitrary digraph.
       <p>
       A compact representation of a reachability matrix is created
       in which reachability queries are guaranteed to take
       <tt>O(log(|V|)</tt> time, and is oftentimes much faster.  
       While the space required by the representation can vary a lot, it is
       typically linear if it is handed a randomly generated graph.
       </p>
       @param dg the graph whose closure is to be computed.
       @return a pseudo-matrix which returns true upon a call
       to <tt>.get(Node u, Node v)</tt> exactly if <tt>v</tt>
       is reachable from <tt>u</tt> in <tt>dg</tt>.  The returned
       matrix throws an UnsupportedOperationException if its
       <tt>set</tt> method is used.
     */
    public static NodeMatrix<Boolean> compactClosure(Digraph dg)
    {
        final Transitivity t = new Transitivity(dg);
        t.run();
        return new NodeMatrix<Boolean>() {
            public Boolean get(Node u, Node v)
            {
                return t.reaches(u,v);
            }
            public Boolean set(Node u, Node v, Boolean b)
            {
                throw new UnsupportedOperationException();
            }
            public void grow(int c) {}
            public void shrink(int c, int[] p) {}
            public void clear() {}
        };
        
    }

    /**
       Compute a reachability matrix for an acyclic digraph.
       <p>
       A reachability matrix allows for constant time reachability querying
       at the expense of the space used to store the matrix.
       If the graph is large or the number of reachability queries
       does not exceed <tt>|V|<sup>2</sup> / log(|V|)</tt>, then using the 
       {@link #compactClosure} method is recomended.
       </p>
       @param dg a digraph.
       @return a matrix whose entries <tt>(i,j)</tt> are <tt>1</tt> if 
       <tt>i</tt> can reach <tt>j</tt> in the graph <tt>dg</tt>, and <tt>0</tt>
       otherwise.
       
     */
    public static ByteNodeMatrix acyclicClosure(Digraph dg)
    {
        return acyclicClosure(dg, TopSorter.topSortList(dg));
    }

    /**
       Compute a reachability matrix for an acyclic digraph.
       <p>
       A reachability matrix allows for constant time reachability querying
       at the expense of the space used to store the matrix.
       If the graph is large or the number of reachability queries
       does not exceed <tt>|V|<sup>2</sup> / log(|V|)</tt>, then using the 
       {@link #compactClosure} method is recomended.
       </p>
       @param dg a digraph.
       @param tsort a topologically sorted list of nodes in <tt>dg</tt>.
       @return a matrix whose entries <tt>(i,j)</tt> are <tt>1</tt> if 
       <tt>i</tt> can reach <tt>j</tt> in the graph <tt>dg</tt>, and <tt>0</tt>
       otherwise.
     */
    public static ByteNodeMatrix acyclicClosure(Digraph dg, CList<Node> tsort)
    {
        Transitivity t = new Transitivity(dg);
        t.run(tsort);
        ByteNodeMatrix m = dg.createByteNodeMatrix();
        for (Node i : dg.nodes()) {
            for (Node j : dg.nodes()) {
                if (t.reaches(i, j))
                    m.set(i, j, (byte) 1);
            }
        }
        return m;
    }

    /**
       Add a minimal number of edges to a mutable digraph to make it transitive.
       @return the number of edges added.
       @param mdg the mutable digraph to be made transitive.
     */
    public static CList<Edge> close(MutableDigraph mdg)
    {
        Transitivity t = new Transitivity(mdg);
        t.run();
        CList<Edge> added = new CList<Edge>();
        int[] marks = new int[mdg.nodeAttrSize()];
        int mark = 1;
        for (Node i : mdg.nodes()) {
            for (Edge e = i.out(); e != null; e = e.next()) {
                e.target().setInt(marks, mark);
            }
            for (Node j : mdg.nodes()) {
                if (t.reaches(i, j) && i != j && j.getInt(marks) < mark) {
                    added.add(mdg.genEdge(i,j));
                }
            }
            mark++;
        }
        return added;      
    }

    /**
       Remove redundant edges from a mutable acyclic digraph.

       @param mdg the mutable acyclic digraph from which to remove
       edges.
       @return a list of the removed edges.
     */
    public static CList<Edge> acyclicReduce(MutableDigraph mdg)
    {
        TopSorter ts = new TopSorter(mdg);
        ts.run();
        CList<Node> tsort = ts.getSort();
        mdg.sortEdges(NodeOrder.getEdgeComparator(ts.order()));
        ByteNodeMatrix m = acyclicClosure(mdg, tsort);
        CList<Edge> remove = new CList<Edge>();
        for (Node i : tsort) {
            for (Edge e = i.out(); e != null; e = e.next()) {
                Node j = e.target();
                if (m.get(i, j) != 0) {
                    for (Edge ee = e.next(); ee != null; ee = ee.next()) {
                        Node k = ee.target();
                        if (m.get(j, k) != 0) {
                            m.set(i, k, (byte) 0);
                        }
                    }
                } else {
                    remove.add(e);
                }
            }
        }
        for (Edge e : remove) {
            mdg.remove(e);
        }
        return remove;
    }

    /**
       Compute a transitive reduction of a given digraph.
       <p>
       A digraph is computed which is a smallest graph whose
       transitive closure includes <tt>g</tt>.
       </p>
       @param g the digraph for computing the reduction
       @param nMap an attribute map which will contain a mapping from nodes in 
       <tt>g</tt> to nodes in the result.
       @return a digraph representing the transitive reduction of <tt>g</tt>.
     */
    public static BasicDigraph reduce(Digraph g, NodeMap<Node> nMap)
    {
        NodeMap<Node> qMap = g.createNodeMap(null);
        BasicDigraph quotient = StrongComponents.quotient(g, qMap);
        // qMap maps nodes in g to nodes in quotient, we're
        // going to grow quotient to the reduction of g now.
        quotient.ensureCapacity(g.nodeSize(), g.edgeSize());
        NodeMap<CList<Node>> compMap = 
            quotient.getNodeMap(StrongComponents.QuotientCompListMapKey);
        // 
        // compMap maps nodes in the quotient to lists of nodes in g
        // such that each node in a list belongs to the component
        // represented by the node in the quotient.
        //
        acyclicReduce(quotient);
        for (Node n : g.nodes()) {
            Node qnNode = qMap.get(n);
            CList<Node> compList = qnNode.get(compMap);
            if (compList.isEmpty()) continue;
            Node cLast = compList.removeFirst();
            Node cFirst = cLast;
            n.set(nMap, qMap.get(cLast));
            //
            // for all remaining nodes in g in the component represented by qnNode
            // we create a new node in quotient and construct a simple cycle
            // from these nodes.
            //
            while(!compList.isEmpty()) {
                Node cNode = compList.removeFirst();
                Node qcNode = quotient.genNode();
                cNode.set(nMap, qcNode);
                quotient.genEdge(qcNode, qMap.get(cLast));
                cLast = cNode;
                if (compList.isEmpty()) {
                    // complete the cycle.
                    quotient.genEdge(qMap.get(cFirst), qMap.get(cLast));
                }
            }
        }
        return quotient;
    }


    private void init()
    {
        this.rangeLists = null;
        this.quotient = null;
        this.scc = new StrongComponents(digraph);
        this.tsorter = null;
        this.pq = new PriorityQueue<IRange>();
        this.start = new int[digraph.nodeAttrSize()];
        this.end = new int[digraph.nodeAttrSize()];
        this.numMerges = 0;
        this.numUnions = 0;
    }

    protected void run()
    {
        reset();
        scc.run();
        quotient = scc.quotient();
        tsorter = new TopSorter(quotient);
        tsorter.run();
        CList<Node> tsort = tsorter.getSort();
        run(tsort);
    }

    protected void run(CList<Node> tsort)
    {
        rangeLists = new IRange[quotient.nodeAttrSize()];
        ArrayList<IRange> iral = 
	    new ArrayList<IRange>(quotient.nodeAttrSize());
        while(tsort.size() > 0) {
            Node n = tsort.removeLast();
            processNode(n, iral);
        }
        ranges = new IRange[iral.size()];
        iral.toArray(ranges);
    }


    protected void processNode(Node n, ArrayList<IRange> iral)
    {
        start[n.nodeId()] = iral.size();
        pq.clear();
        for(Edge e = n.out(); e != null; e = e.next()) {
            pq.add(rangeLists[e.target().nodeId()]);
        }
        IRange current = new IRange(0, 0);
        rangeLists[n.nodeId()] = current;
        IRange min = null;
        while((min = pq.poll()) != null) {
            if (IRange.mergeable(current, min)) {
                numMerges++;
                current.merge(min);
            } else {
                current.next = new IRange(0, 0);
                iral.add(current);
                current = current.next;
            }
            numUnions++;
            if (min.next() != null) {
                pq.offer(min.next());
            }
        }
        iral.add(current); 
        int tsNum = tsorter.tsNum(n);
        IRange me = new IRange(tsNum, tsNum+1);
        if (IRange.mergeable(current, me)) {
            numMerges++;
            current.merge(me);
        } else {
            current.next = me;
            iral.add(current.next);
        }
        numUnions++;
        end[n.nodeId()] = iral.size() - 1;
    }

    /**
       Computes the proportion of interval merging operations <tt>m</tt>to
       interval union <tt>u</tt> operations.

       @return <tt>m/u</tt>
     */
    protected double mergeRatio()
    {
        return ((double) numMerges) / ((double) numUnions);
    }

    /**
       Return true if <tt>s</tt> reaches <tt>t</tt> in the original
       graph.

       This query takes <tt>log(n)</tt> time where <tt>n</tt> is 
       the total number of distinct intervals used to describe
       the reachable set of nodes in the quotient graph from 
       the quotient of <tt>s</tt>.
     */
    protected boolean reaches(Node s, Node t)
    {
        int sc = scc.component(s);
        int tc = scc.component(t);
        if (sc == tc) return true;
        int low = start[sc];
        int high = end[sc];
        int mid = low + ((high - low) / 2);
        int target = tsorter.tsNum(quotient.node(tc));
        IRange tr = new IRange(target, target + 1);
        while(true) {
            if (ranges[mid].contains(target)) 
                return true;
            int c = ranges[mid].compareTo(tr);
            if (c < 0) {
                high = mid;
                mid = low + ((high - low) / 2);
            } else if (c > 0) {
                low = mid;
                mid = low + ((high - low) / 2);
            } else {
                assert false;
            }
            if (high - low <= 1) {
                return ranges[low].contains(target) 
		    || ranges[high].contains(target);
            }
        }
    }

    /**
       Returns the total number of nodes in the quotient graph.
     */
    int quotientNodeSize() 
    {
        return quotient.nodeSize();
    }

    /**
       Returns the total number of ranges used to represent the 
       transitive closure.
     */
    int nRanges()
    {
        return ranges.length;
    }

    /*
      Implement algorithm.
     */
    void reset()
    {
        init();
    }
}


class IRange implements Comparable<IRange>
{
    protected int lower;
    protected int upper;

    protected IRange next;


    public IRange(int l, int u)
    {
        assert l <= u;
        lower = l;
        upper = u;
    }

    public final int compareTo(final IRange or)
    {
        return lower < or.lower ? -1 : (lower == or.lower ? 0 : 1);
    }

    public static final boolean mergeable(final IRange r1, final IRange r2)
    {
        // empty intervals are always mergeable.
        if (r1.upper == r1.lower || r2.upper == r2.lower)
            return true;

        IRange rmin = r1.lower <= r2.lower ? r1 : r2;
        IRange rmax = r1.lower > r2.lower ? r1 : r2;
        return rmin.upper >= rmax.lower;
    }

    public void merge(IRange r)
    {
        if (upper == lower) {
            upper = r.upper; lower = r.lower;
            return;
        } 
        if (r.upper == r.lower) {
            return;
        }
        upper = upper >= r.upper ? upper : r.upper;
        lower = lower <= r.lower ? lower : r.lower;
    }

    public final IRange next() { return next; }

    public final boolean contains(int i) 
    {
        return i >= lower && i < upper;
    }

    public String toString() 
    {
        return "[" + lower + ".." + upper + ")";
    }
}

