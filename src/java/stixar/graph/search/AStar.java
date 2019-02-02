package stixar.graph.search;

import stixar.graph.Graph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.paths.Dijkstra;
import stixar.graph.attr.NodeMap;
import stixar.graph.attr.EdgeSource;

import stixar.util.NumAdaptor;
import stixar.util.fheap.FibHeap;

import java.util.Comparator;


/**
   Class for A<sup>*</sup> search.

   <p>
   A<sup>*</sup> search is a heuristic based best first search
   mechanism.  Given an edge weighted graph, a source vertex <tt>s</tt>,
   a target vertex <tt>t</tt>, and a heuristic function
   <tt>h</tt> which gives estimated distances to the target from 
   every vertex, A<sup>*</sup> search works by processing nodes in a best
   first manner, where "best" is defined in terms of 
   <center><tt>d(v) + h(v)</tt></center>
   where <tt>d(v)</tt> is the known exact distance from the source to <tt>v</tt>
   and <tt>h(v)</tt> estimates the remaining distance to the target.
   </p>
   <p>
   The heuristic function should have the property that 
   <tt>h(u) + weight(u,v) &gt;= h(v)</tt>.  This property is necessary to guarantee
   that the search takes less time than a search without the heuristic. 
   </p>
 */
public class AStar
{

    private AStar() {}
    /**
       A Star search node comparator.
     */
    public static class Cmp<T> implements Comparator<Node>
    {
        protected NodeMap<T> distMap;
        protected NodeMap<T> heuristic;
        protected NumAdaptor<T> adaptor;
        
        public Cmp(NodeMap<T> distMap,
                   NodeMap<T> heuristic,
                   NumAdaptor<T> adaptor)
        {
            this.distMap = distMap;
            this.heuristic = heuristic;
            this.adaptor = adaptor;
        }

        public int compare(Node n1, Node n2)
        {
            T v1 = adaptor.add(distMap.get(n1), heuristic.get(n1));
            T v2 = adaptor.add(distMap.get(n2), heuristic.get(n2));
            return adaptor.compare(v1, v2);
        }
    }

    /**
       Compute shortest path from source to target following a heuristic.
       @param g the graph in which to perform the search.
       @param source the source vertex.
       @param target the target vertex.
       @param weights edge weights for the graph.
       @param distMap a map in which to store node distances.
       @param heuristic a heuristic function estimating node distances to
       the target
       @param predMap a map associating each node with its predecessor edge
       in the shortest path tree.
       @param adaptor a shortests paths adaptor for the problem.
     */
    public static <T> void search(Graph g, 
                                  Node source,
                                  Node target,
                                  EdgeSource<T> weights,
                                  NodeMap<T> distMap,
                                  NodeMap<T> heuristic,
                                  NodeMap<Edge> predMap,
                                  NumAdaptor<T> adaptor)
    {
        Dijkstra<T> a = new Dijkstra<T>(g, source, target, predMap,
                                        distMap, weights, adaptor, null);
        a.setPQueue(new FibHeap<Node>(new Cmp<T>(distMap, heuristic, adaptor)));
        a.run();
    }
}
