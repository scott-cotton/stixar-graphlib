package stixar.graph.gen;

import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.Digraph;
import stixar.graph.BasicDigraph;
import stixar.graph.BasicNode;
import stixar.graph.BasicEdge;
import stixar.graph.attr.NodeMap;

import stixar.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
   A Factory class for digraphs made up of 
   {@link BasicNode basic nodes} and {@link BasicEdge basic edges}.
   <p>
   In general one uses this factory as follows:
   <pre>
   BasicDGFactory fact = new BasicDGFactory();
   Node n1 = fact.node();
   Node n2 = fact.node();
   Edge e1 = fact.edge(n1, n2);
   Edge e2 = fact.edge(n1, n2); // multiple edges allowed.
   // ...
   Digraph dg = fact.digraph();
   </pre>

   In the example above, <tt>dg</tt> is comprised of all the edges
   and nodes created since the factory was instantiated.
   <p>
   Each instance of this factory class is capable of producing several
   Digraphs comprised of BasicNodes and BasicEdges.  However,
   the generated digraphs are unrelated and the factory is reset after
   each digraph is generated.
   </p>
   </p>
 */
public class BasicDGFactory 
{
    protected ArrayList<BasicNode> nodes;
    protected HashMap<Pair<Integer,Integer>, BasicEdge> edgeMap;
    protected Random rnd = new Random();
    protected BasicDigraph digraph;
    

    /**
       Create a factory for making digraphs out of
       {@link BasicNode}s and {@link BasicEdge}s.
     */
    public BasicDGFactory() 
    {
        this(128, 512);
    }

    /**
       Create a factory with room allocated for <tt>n</tt> nodes
       and <tt>m</tt>edges.
     */
    public BasicDGFactory(int n, int m)
    {
        nodes = new ArrayList<BasicNode>();
        edgeMap = new HashMap<Pair<Integer,Integer>, BasicEdge>();
        digraph = new BasicDigraph(n, m);
    }

    /**
       Construct a topologic copy of a digraph, packing the 
       ids of the nodes and edges.
       The resulting graph is not guaranteed to share attribute
       information, ie it is identical to <tt>dg</tt> up to 
       topology but corresponding nodes may have different identifiers.
       For attribute preserving copy, see {@link BasicDigraph#copy}.
     */
    public BasicDGFactory(Digraph dg)
    {
        this(dg.nodeSize(), dg.edgeSize());
        genNodes(dg.nodeSize());
        int i=0;
        NodeMap<Node> nMap = dg.createNodeMap(null);
        for (Node n : dg.nodes()) {
            nMap.set(n, digraph.node(i++));
        }
        for (Edge e : dg.edges()) {
            digraph.genEdge(nMap.get(e.source()), nMap.get(e.target()));
        }
    }

    /**
       Create a new BasicNode
     */
    public BasicNode node()
    {
        return digraph.genNode();
    }

    /**
       Generate a number of edges randomly, disallowing multiple edges and
       self loops.

       @param nEdges the number of edges to generate.
       @throws IllegalArgumentException if <tt>nEdges &gt; |nodes| * |nodes|</tt>
     */
    public void genEdges(int nEdges)
    {
        genEdges(nEdges, false, false);
    }

    /**
       Generate a number of edges randomly, specifying whether multiple edges
       or self loops are allowed.

       @param nEdges the number of edges to generate.
       @param allowMultiple whether to allow more than one edge between
       a given pair of vertices.
       @param allowSelf whether to allow self loops.
       @throws IllegalArgumentException if <tt>nEdges &gt; |nodes| * |nodes|</tt>
       and <tt>allowMultiple</tt> is false.
     */
    public void genEdges(int nEdges, boolean allowMultiple, boolean allowSelf)
    {
        int ttl = 0;
        int nsz = digraph.nodeSize();
        while(ttl < nEdges) {
            int sid = rnd.nextInt(nsz);
            int tid = rnd.nextInt(nsz);
            if (!allowSelf && sid == tid) {
                continue;
            }
            Pair<Integer,Integer> key = new Pair<Integer,Integer>(sid, tid);
            if (!allowMultiple && edgeMap.containsKey(key)) {
                continue;
            }
            BasicNode s = digraph.node(sid);
            BasicNode t = digraph.node(tid);
            BasicEdge e = digraph.genEdge(s, t);
            edgeMap.put(key, e);
            ttl++;
        }
    }

    /**
       Generate a number of new nodes.

       @param n the number of nodes to generate.
     */
    public void genNodes(int n)
    {
        digraph.genNodes(n);
    }

    /**
       Create source and sink vertices.
       <ul>
       <li>
       A <em>source vertex</em> is a vertex connected to each vertex
       in the graph (other than itself and the sink vertex).
       </li>
       <li>
       A <em>sink vertex</em> is a vertex to which every vertex in
       the graph (other than the source and the sink) is connected.
       </li>
       </ul>

       <p>
       Note this operation is different than subsequent calls
       to {@link #genSource} and {@link #genSink} because the 
       source is not directly connected to the sink.
       </p>

       @return a pair of nodes whose first element is
       a source for the graph and whose second element
       is a sinnk for the graph.
     */
    public Pair<BasicNode,BasicNode> genSourceAndSink()
    {
        BasicNode src = node();
        int srcId = src.nodeId();
        for (int i=0; i<srcId; ++i) {
            digraph.genEdge(digraph.node(srcId), digraph.node(i));
        }
        BasicNode snk = node();
        int snkId = snk.nodeId();
        for (int i=0; i<srcId; ++i) {
            digraph.genEdge(digraph.node(i), digraph.node(snkId));
        }
        return new Pair<BasicNode,BasicNode>(src,snk);
    }

    /**
       Create a new source vertex.
       <p>
       A <em>source vertex</em> is a vertex connected to each vertex
       in the graph (other than itself and the sink vertex).
       </p>
       @return a source vertex.
     */
    public BasicNode genSource()
    {
        BasicNode res = node();
        int resId = res.nodeId();
        for (int i=0; i<resId; ++i) {
            digraph.genEdge(digraph.node(resId), digraph.node(i));
        }
        return res;
    }


    /**
       Create a new sink vertex.
       
       <p>
       A <em>sink vertex</em> is a vertex to which every vertex in
       the graph (other than the source and the sink) is connected.
       </p>

       @return a sink vertex.
     */
    public BasicNode genSink()
    {
        BasicNode res = node();
        int resId = res.nodeId();
        for (int i=0; i<resId; ++i) {
            digraph.genEdge(digraph.node(i), digraph.node(resId));
        }
        return res;
    }


    /**
       Create a new BasicEdge.
     */
    public BasicEdge edge(BasicNode s, BasicNode t)
    {
        return digraph.genEdge(s, t);
    }

    /**
       Tell whether or not an edge exists between a source and 
       target node.

       @param sid the identifier of the source node.
       @param tid the identifier of the target node.
       @return true iff there is an edge from the node s
       with id <tt>sid</tt> to the node t with id <tt>tid</tt>.
     */
    public boolean containsEdge(int sid, int tid)
    {
        Pair<Integer,Integer> key = new Pair<Integer,Integer>(sid, tid);
        return edgeMap.containsKey(key);
    }


    /**
       Create a new BasicEdge.
     */
    public BasicEdge edge(int sid, int tid)
    {
        BasicNode s = digraph.node(sid);
        BasicNode t = digraph.node(tid);
        BasicEdge e = digraph.genEdge(s, t);
        edgeMap.put(new Pair<Integer, Integer>(sid, tid), e);
        return e;
    }

    /**
       Create a digraph comprised of all the nodes and edges
       produced by this factory since this factory was created.
     */
    public BasicDigraph digraph()
    {
        return digraph;
    }
}
