package stixar.util;

/**
   Opaque interface for a cell in a collection such as
   a linked list or priority queue.
 */
public interface Cell<E>
{
    /**
       Return the value associated with the cell
     */
    public E value();

    /**
       Return true if the cell is still a valid cell in the
       collection which generated it.
       <p>
       It is obligatory for implementations to make this
       method return true if and only if the container
       associated with the implementation will be behave properly
       upon being passed a cell.  IE, if a list produces a cell
       and then asks it to be removed, then the cell may
       survive but after the removal it should return false
       upon a call to <tt>isValid()</tt>.
       </p>
    */
    public boolean isValid();

}

