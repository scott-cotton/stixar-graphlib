package stixar.graph;

import java.util.List;

import stixar.util.CList;

/**
   A class which implements a graph filter by 
   <tt>or</tt>ing together a list of graph filters.

   <p>
   Given a list of filters, the class
   creates a filter which will filter a node or edge just in case
   there is a filter in the list which will filter that node or edge.
   </p>
 */
public class ListGraphFilter implements GraphFilter
{
    protected CList<GraphFilter> filters;


    public ListGraphFilter(List<GraphFilter> fList)
    {
        filters = new CList<GraphFilter>();
        filters.addAll(fList);
    }

    public final boolean filter(Node n)
    {
        for (GraphFilter f : filters) {
            if (f.filter(n))
                return true;
        }
        return false;
    }

    public final boolean filter(Edge e)
    {
        Node s = e.source();
        Node t = e.target();
        for (GraphFilter f : filters)
            if (f.filter(s) || f.filter(t) || f.filter(e))
                return true;
        return false;
    }
}
