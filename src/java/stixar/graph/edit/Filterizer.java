package stixar.graph.edit;

import stixar.graph.MutableGraph;
import stixar.graph.Node;
import stixar.graph.Edge;
import stixar.graph.GraphFilter;

import stixar.util.CList;

import java.util.HashMap;
import java.util.Map;

/**
   Permanently remove filtered nodes and edges.
 */
public class Filterizer
{
    CList<Node> fNodes;
    CList<Edge> fEdges;
    GraphFilter filter;

    /**
       Construct a new Filterizer with a GraphFilter.
     */
    public Filterizer(GraphFilter filt)
    {
        fNodes = new CList<Node>();
        fEdges = new CList<Edge>();
        filter = filt;
    }

    /**
       Edit the graph <tt>mg</tt> in place, applying the filter <tt>f</tt>.
       The default filter is in addition set to <tt>f</tt>.
     */
    public void edit(GraphFilter f, MutableGraph mg)
    {
        filter = f;
        edit(mg);
    }

    /**
       Edit the graph <tt>mg</tt> in place, applying the filter.
     */
    public void edit(MutableGraph mg)
    {
        fNodes.clear();
        fEdges.clear();
        for (Node n : mg.nodes()) {
            boolean nf = filter.filter(n);
            if (nf) fNodes.add(n);
            for (Edge e = n.out(); e != null; e = e.next()) {
                if (nf || filter.filter(e))
                    fEdges.add(e);
            }
        }
        for (Edge e : fEdges) mg.remove(e);
        for (Node n : fNodes) mg.remove(n);
    }

    /**
       Undo a filterizer edit by generating new nodes and edges.

       A topologic copy of the edited subgraph is added to <tt>mg</tt>.
       The new nodes may not be equal to the filtered nodes, but
       it is guranteed that for each filtered node there is one
       topologically identical node created.  The mapping from
       the filtered nodes to the new nodes is returned.
       @param mg a mutable graph to edit.
       @return a mapping from the old filtered nodes to the new nodes.
     */
    public Map<Node,Node> unEdit(MutableGraph mg)
    {
        HashMap<Node,Node> nMap = new HashMap<Node,Node>();
        for (Node n : fNodes)
            nMap.put(n, mg.genNode());
        for (Edge e : fEdges) {
            Node gs = nMap.get(e.source());
            if (gs == null) 
                gs = e.source();
            Node gt = nMap.get(e.target());
            if (gt == null)
                gt = e.target();
            mg.genEdge(gs, gt);
        }
        return nMap;
    }

    /**
       Return the set of removed nodes.
     */
    public CList<Node> filteredNodes()
    {
        return fNodes;
    }

    /**
       Return the set of removed edges.
     */
    public CList<Edge> filteredEdges()
    {
        return fEdges;
    }
}
