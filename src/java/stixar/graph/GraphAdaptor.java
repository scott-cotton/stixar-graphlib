package stixar.graph;

/**
   Generate graphs from arbitrary {@link Node} and {@link Edge} 
   implementations.
 */
public class GraphAdaptor 
{
    /**
       Create a read-only directed graph from an array of nodes.
       <p>
       The nodes and any edges connected to them should respect
       the semantics of directed graphs.
       </p>
       @param nodes the complete set of nodes in the graph
       @param esz the number of edges found in the graph.
       @return a Digraph which can then be used with many of the 
       algorithms implemented here.
       @see Digraph
     */
    public static Digraph digraph(Node[] nodes, int esz)
    {
        return new DiAdjList(nodes, esz);
    }

    /**
       Create a read-only directed graph from an array of nodes.
       <p>
       The graph is traversed in order to count the number of edges.
       </p>
       <p>
       The nodes and any edges connected to them should respect
       the semantics of directed graphs.
       </p>
       @param nodes the complete set of nodes in the graph
       @return a Digraph which can then be used with many of the 
       algorithms implemented here.
       @see Digraph
     */
    public static Digraph digraph(Node[] nodes)
    {
        return new DiAdjList(nodes);
    }

    /**
       Create a read-only un directed graph from an array of nodes.
       <p>
       The nodes and edges should respect the general contract of 
       undirected graphs.
       </p>
       @param nodes the complete set of nodes in the graph
       @param esz the number of edges found in the graph.
       @return an undirected graph which can then be used with many of the 
       algorithms implemented here.
       @see UGraph
     */
    public static UGraph ugraph(Node[] nodes, int esz)
    {
        return new UAdjList(nodes, esz);
    }

    /**
       Create a read-only un directed graph from an array of nodes.
       <p>
       The nodes and edges should respect the general contract of 
       undirected graphs.  The edges of the graph are traversed in
       order to count the number of edges.
       </p>
       @param nodes the complete set of nodes in the graph
       @return an undirected graph which can then be used with many of the 
       algorithms implemented here.
       @see UGraph
     */
    public static UGraph ugraph(Node[] nodes)
    {
        return new UAdjList(nodes);
    }
}