package stixar.graph;

import stixar.graph.attr.AttrManager;

import stixar.util.CList;
import stixar.util.ListCell;


/*
  Note about documentation maintenance:

  The javadocs in here are duplicates of the Digraph javadocs and
  are necessary because java's standard doclet doesn't use the 
  Digraph javadocs since this base class doesn't explicitly implement
  Digraph.
 */

/*
  A filter digraph is a non-public base class which implements the
  filtering portion of the Digraph interface.  It is subclassed
  by AdjList, BasicDigraph, and BasicPDigraph.
 */
class FilterGraph extends AttrManager
{

    protected CList<GraphFilter> filters;

    protected FilterGraph(int nCap, int eCap)
    {
        this(nCap, eCap, null);
    }

    protected FilterGraph(int nCap, int eCap, GraphFilter f)
    {
        super(nCap, eCap);
        this.filters = new CList<GraphFilter>();
        if (f != null)
            addFilter(f);
    }

    /**
       Add a graph filter, restricting the nodes and/or edges in the graph.
     */
    public void addFilter(GraphFilter f)
    {
        filters.addFirst(f);
    }

    /**
       Remove the last added graph filter.

       @return the last added graph filter, or <tt>null</tt> if
       no such filter exists.
     */
    public GraphFilter removeFilter()
    {
        if (filters.isEmpty()) return null;
        return filters.removeFirst();
    }

    /**
       Clear all the filters.
     */
    public void clearFilters()
    {
        filters.clear();
    }

    /**
       Return the currently installed filter for this graph,
       which may be a {@link ListGraphFilter} implementing all
       the added GraphFilters.

       @return an GraphFilter for this digraph, or <tt>null</tt> if
       no such filter exists.
     */
    public GraphFilter getFilter()
    {
        if (filters.isEmpty())
            return null;
        else if (filters.size() == 1)
            return filters.getFirst();
        else
            return new ListGraphFilter(filters);
    }

}
