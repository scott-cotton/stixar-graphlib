package stixar.util.fheap;

import stixar.util.Cell;

/**
   public interface to an item in a Fibonacci heap.

   @see FibHeap#increasePriority
 */
public class FHeapCell<E>  implements Cell<E>
{
    protected E element;
    protected boolean valid;

    protected FHeapCell(E elt)
    {
        element = elt;
        valid = true;
    }

    /**
       Returns the actual item in the heap.

       @return the actual item in the heap.
     */
    public final E value() { return element; }

    /**
       Tell whether or not the cell is still a valid part of the
       fib heap.
     */
    public final boolean isValid() { return valid; }

    // for use by FibHeap
    final E value(E elt) { return element = elt; }
    final void valid(boolean v) { valid = v; }
}
