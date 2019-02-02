
package stixar.graph;

/**
   Marker interface for algorithms that accept graphs with
   node or edge filters.

   <p>
   Filtering is implemented by cooperation.  This means that
   in this framework, a filter does not filter in all contexts.
   In particular, we designate algorithms as filtering if they
   respect the filters in a digraph.  The digraphs themselves 
   provide methods for iterating over nodes and edges, and those
   methods <bf>do not</bf> respect the filtering conventions.
   </p>

   <p>
   This approach to filtering is lightweight and simplifies the representation
   of nodes and edges considerably, saving a great deal of space when the
   graphs are large, and also facilitating the implementation of the {@link
   Node} and {@link Edge} interfaces by application specific data structures.
   The drawback to this style of filtering is that the user must understand
   that only certain algorithms respect the filtering protocol in order to keep
   track what seems to be present in a graph and when.
   </p>

   @see Graph
   @see GraphFilter
 */
public interface Filtering
{
}
