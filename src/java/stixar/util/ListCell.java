package stixar.util;

/**
   An interface to a list cell.

   <p>
   Given a function <tt>f</tt> to create a list cell from a collection,
   such as {@link CList#firstCell}, a list cell may be used as an iterator 
   in the following form

   <pre>
   for (ListCell<T> cell = f(); cell != null; cell = cell.next())
   </pre>
   
   <h3>Warning</h3> The {@link Cell#isValid} method is <em>not</em> intended for
   iteration as in
   <pre>
   for (ListCell<T> cell = f(); cell.isValid(); cell = cell.next())
   </pre>

   rather, see the general contract for {@link Cell#isValid}.
   </p>
 */
public interface ListCell<T> extends  Cell<T>
{

    /**
       Return the next cell in the list, or <tt>null</tt> if
       no such cell exists.
     */
    public ListCell<T> next();

    /**
       Return the previous cell in the list, or <tt>null</tt>
       if no such cell exists.
     */
    public ListCell<T> prev();

    /**
       Set the value associated with the cell.
     */
    public T value(T v);
}
