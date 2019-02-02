package stixar.graph;

/**
   A filter for nodes and edges in a graphs.  Filtering is a lightweight
   mechanism enforced only by cooperation.  Please see the documentation
   on {@link Filtering} for a description and listing of what contexts 
   respect filters.
 */
public interface GraphFilter
{

    /**
       @param e an edge to be tested for conformance in a graph.
       @return true iff the edge <tt>e</tt> is to be filtered 
       out of the graph.
     */
    public boolean filter(Edge e);

    /** 
        return true if the node should be skipped during a 
        {@link Filtering} {@link Algorithm} 
    */
    public boolean filter(Node n);

}
