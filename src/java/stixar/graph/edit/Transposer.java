package stixar.graph.edit;

import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.MutableDigraph;


import java.util.ArrayList;

/**
   Compute transpose for an editable digraph.
 */
public class Transposer
{
    protected ArrayList<Edge> edges;

    /**
       Create a new transposer.
     */
    public Transposer()
    {
        edges = new ArrayList<Edge>();
    }

    /**
       Transpose the editable digraph <tt>edg</tt> in-place.
       Every edge from <tt>s</tt> to <tt>t</tt> is changed
       so that it becomes an edge from <tt>t</tt> to <tt>s</tt>.
      
       @param mdg the editable digraph to be transposed.
     */
    public void edit(MutableDigraph mdg)
    {
        edges.clear();
        for (Edge edge: mdg.edges()) {
            edges.add(edge);
        }
        for (Edge edge: edges) {
            Node source = edge.source();
            Node target = edge.target();
            mdg.moveEdge(edge, target, source);
        }
    }
}
