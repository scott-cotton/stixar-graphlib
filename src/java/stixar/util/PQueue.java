package stixar.util;

/**
   Interface for a priority queue.
   <p>
   This interface differs from the standard Jata<sup>TM</sup> one because
   we find it unintuitive to work without something resembling C's 
   pointers or C++ STL iterators.  In particular, if one has a collection
   of elements of type E,  then in order to remove, increase priority,
   etc in constant time, one cannot pass in the element to be removed
   because:
   <ol>
   <li> There might be more than one such element.</li>
   <li> A search for the element must take place.</li>
   </ol>
   
   While one can work around in this in using Java<sup>TM</sup> collections
   by either
   <ol>
   <li>cloning iterators<br></br>
   or 
   </li>
   <li> putting the pointer/iterator functionality in the element of type E
   by creating a new E for each contained type.
   </li>
   </ol>

   There are obvious problems (1 iterators do not extend Cloneable so
   one cannot clone them, 2 is just plain awkward).

   we have our own priority queue interface.
*/
public interface PQueue<E> extends Iterable<E>
{
    /**
       Insert an element into the priority queue.
       @param elt the element to insert
       @return a Cell which gives a handle on the element's position
       in the priority queue.
     */
    public Cell<E> insert(E elt);

    /**
       Extract the highest priority element, or element with the least
       value as determined by the priority queues comparator.  Return
       <tt>null</tt> if the queue is empty.
     */
    public E extractMin();

    /**
       return the minimum valued item in the queue, or <tt>null</tt> if
       the queue is empty.
     */
    public E min();

    /**
       Increase the priority of the element contained in a cell.
       @param cell the cell containing the element whose priority
       is to be increased.
     */
    public void requeue(Cell<E> cell);

    /**
       Clear the priority queue so that it contains no items.
     */
    public void clear();

    /**
       Return whether or not the priority queue is empty.
     */
    public boolean isEmpty();

    /**
       Return the size of the priority queue.
     */
    public int size();
}