package stixar.util;

import java.io.Serializable;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;


/**
   This class is just like a java LinkedList, except ...

   <ul>
   <li>It exposes the list nodes so 
   that the programmer can more easily efficiently store locations
   of the list for later retrieval and make threaded data structures
   on top of lists.</li>
   <li>offers some additional constant time list operations such as concatenation
   and removal via a cell.
   </li>
   </ul>
   <p>
   It is more or less completely interoperable with <tt>java.util.LinkedList</tt>,
   with the following exceptions:

   <ul>
   <li>{@link #clear} does not take O(n) time, it is constant time 
   and depends on the garbage collector to deal with the cyclic references.
   </ul>

   <p>
   It is called CList because it exposes the list cells to the programmer.
   </p>

   This is not synchronized. If multiple threads are to access this list,
   use <tt>Collections.synchronizedList()</tt>.

   <p>
   Although, as in <tt>java.util.LinkedList</tt>,  the iterators make a best 
   effort at fail-fast commodification detection, the programmer may perform 
   commodification when iterating via the {@link ListCell}s, so long as no plain 
   <tt>Iterator</tt> is iterating over the list.
   </p>
 */
public class CList<T> 
    implements List<T>, Queue<T>, Cloneable, Serializable
{

    protected transient Node<T> sentinel;
    protected transient int size;
    protected transient int numMods;

    /**
       Construct a new CList.
     */
    public CList()
    {
        this.sentinel = new Node<T>(this, (T) null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        this.size = 0;
        this.numMods = 0;
    }

    /**
       Create a shallow copy of <tt>cList</tt>.
       @param cList the list to copy.
     */
    public CList(CList<T> cList)
    {
        this();
        for (T e : cList) {
            append(e);
        }
    }

    /**
       Implement clonable (shallow copy).
     */
    public CList<T> clone()
    {
        return new CList<T>(this);
    }


    /**
       Produce an iterator over the elements in this list.
     */
    public final ListIterator<T> iterator()
    {
        return new NodeIterator<T>(sentinel.next, this);
    }

    /**
       Produce an iterator over the elements in this list.
     */
    public final ListIterator<T> listIterator()
    {
        return iterator();
    }

    /**
       Produce an iterator over elements starting at position <tt>i</tt>
       in this list.
     */
    public final ListIterator<T> listIterator(int i)
    {
        ListIterator<T> it = listIterator();
        while(i-- >= 0 && it.hasNext()) it.next();
        return it;
    }

    /**
       Get the first value in the list.
     */
    public final T first()
    {
        return size == 0 ? null : sentinel.next.value;
    }

    /**
       Get the first node in the list.
     */
    public final ListCell<T> firstCell()
    {
        return size == 0 ? null : sentinel.next;
    }

    /**
       Get the last node in the list.
     */
    public final ListCell<T> lastCell()
    {
        return size == 0 ? null : sentinel.prev;
    }

    /**
       Return the last element in the list, or <tt>null</tt> 
       if the list is empty.
       @return the last element in the list.
     */
    public final T last()
    {
        return size == 0 ? null : sentinel.prev.value;
    }

    /**
       Add <tt>value</tt> to the end of this list.
     */
    public final boolean add(T value)
    {
        append(value);
        return true;
    }

    /**
       Append a value to the list.
     */
    public final ListCell<T> append(T value)
    {
        numMods++;
        Node<T> n = new Node<T>(this, value);
        sentinel.prev.next = n;
        n.prev = sentinel.prev;
        n.next = sentinel;
        sentinel.prev = n;
        size++;
        return n;
    }

    /**
       Prepend a value to the list.
     */
    public final ListCell<T> prepend(T value)
    {
        numMods++;
        Node<T> n = new Node<T>(this, value);
        sentinel.next.prev = n;
        n.next = sentinel.next;
        n.prev = sentinel;
        sentinel.next = n;
        size++;
        return n;
    }

    /**
       Remove the element with cell <tt>cell</tt> from this list.
       <p>
       This is a constant time operation.  
       </p>
       <b>Note</b>: While a check is made
       in an attempt to verify that <tt>cell</tt> belongs to this list, 
       the check may not succeed if the this list was constructed
       in part by the {@link #append(CList)} or the 
       {@link #prepend(CList)} methods.  If these methods are 
       are not used, an <tt>IllegalArgumentException</tt> is 
       guaranteed to be thrown if the cell does not belong
       to this list.  Otherwise, if <tt>cell</tt> does not belong to
       this list, either <tt>IllegalArgumentException</tt>
       is thrown or the list becomes corrupt, indicating the wrong size.
       </p>
       @param cell the list cell to be removed from this list.
     */
    @SuppressWarnings("unchecked")
    public final T remove(ListCell<T> cell)
    {
        Node<T> node = (Node<T>) cell;
        // this check would blow up in combination with append(list)/prepend(list)
        // if numModes < 0 is not checked.  this makes this check not fullproof,
        // but may still be quite helpful in many circumstances.
        if (node.list == null || node.list != this || node.list.numMods < 0) {
            if (node.list == null)
                throw new IllegalArgumentException
                    ("cell already removed.");
            else if (node.list != this)
                throw new IllegalArgumentException
                    ("cell not associated with this list");
            else 
                throw new ConcurrentModificationException();
        }
        numMods++;
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        node.list = null;
        return node.value;
    }

    /**
       Destroy <tt>list</tt> by making it the end of this list.
       At the end of this method, <tt>list</tt> is empty
       and its elements are concatenated to the end of 
       this list.  The operation takes constant time.
       <p>
       <h3>Warning</h3>Use of this method is likely to 
       reduce the efficacy of the fail-fast-ness of the iterators 
       and the efficacy of checks when removing by list cell.

       @throws IllegalArgumentException if called with this.append(this);
     */
    public final void append(CList<T> list)
    {
        if (list == this) 
            throw new IllegalArgumentException
                ("cannot append myself to myself");
        numMods++;
        if (list.size > 0) {
            list.numMods = Integer.MIN_VALUE;
            sentinel.prev.next = list.sentinel.next;
            list.sentinel.next.prev = sentinel.prev;

            list.sentinel.prev.next = sentinel;
            sentinel.prev = list.sentinel.prev;
            size += list.size;
            list.size = 0;
            list.sentinel.next = list.sentinel;
            list.sentinel.prev = list.sentinel;
            //list.clear();
        }
    }

    /**
       Destroy <tt>list</tt> by making it the beginning of this list.

       <p>
       <h3>Warning</h3>Use of this method is likely to 
       reduce the efficacy of the fail-fast iterators.

       @throws IllegalArgumentException if called with this.append(this);
     */
    public final void prepend(CList<T> list)
    {
        if (list == this)
            throw new IllegalArgumentException
                ("cannot prepend myself to myself");
        numMods++;
        if (list.size > 0) {

            list.numMods = Integer.MIN_VALUE;
            list.sentinel.prev.next = sentinel.next;
            sentinel.next.prev = list.sentinel.prev;

            sentinel.next = list.sentinel.next;
            sentinel.next.prev = sentinel;
            size += list.size;
            list.size = 0;
            list.sentinel.next = list.sentinel;
            list.sentinel.prev = list.sentinel;
        }
    }

    /**
       Add the element <tt>elt</tt> to the beginning of the list.
     */
    public final void addFirst(T elt)
    {
        prepend(elt);
    }

    /**
       Add the element <tt>elt</tt> to the end of the list.
     */
    public final void addLast(T elt)
    {
        append(elt);
    }

    /**
       Return the first element in the list.
       @return the first element in the list
       @throws NoSuchElementException if the list is empty.
     */
    public final T getFirst()
    {
        if (size == 0)
            throw new NoSuchElementException();
        return sentinel.next.value;
    }

    /**
       Return the last element in the list.
       @return the last element in the list
       @throws NoSuchElementException if the list is empty.
     */
    public final T getLast()
    {
        if (size == 0)
            throw new NoSuchElementException();
        return sentinel.prev.value;
    }

    /**
       Removes and returns the first element in the list.
       @return the first element in the list
       @throws NoSuchElementException if the list is empty.
     */
    public final T removeFirst()
    {
        if (size == 0)
            throw new NoSuchElementException();
        numMods++;
        T res = sentinel.next.value;
        remove(sentinel.next);
        return res;
    }

    /**
       Removes and returns the last element in the list.
       @return the first element in the list
       @throws NoSuchElementException if the list is empty.
     */
    public final T removeLast()
    {
        if (size == 0)
            throw new NoSuchElementException();
        numMods++;
        T res = sentinel.prev.value;
        remove(sentinel.prev);
        return res;
    }
    
    /**
       Return the number of elements in this list.
       @return the number of elements in this list.
     */
    public final int size()
    {
        return size;
    }

    /**
       Get the <tt>i</tt>th element.
     */
    public final T get(int i)
    {
        Node<T> tmp = sentinel.next;
        int j = 0;
        while(tmp != sentinel) {
            if (i == j++)
                return tmp.value;
            tmp = tmp.next;
        }
        throw new IndexOutOfBoundsException("stixar.util.CList: " + i);
    }

    /**
       Add all elements of the collection <tt>c</tt> to the end of
       this list.

       @param c the collection whose items are appended to this list.
       @throws NullPointerException if <tt>c</tt> is null.
     */
    public final boolean addAll(Collection<? extends T> c)
    {
        for (T v : c) append(v);
        return true;
    }


    /**
       Unsupported operation.
     */    
    public final boolean addAll(int i, Collection<? extends T> c)
    {
        throw new UnsupportedOperationException();
    }

    /**
       Unsupported operation.
     */
    public final boolean containsAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    /**
       remove the object <tt>o</tt> from the list.
     */
    public final boolean remove(Object o)
    {
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            if (o == null ? n.value == null : o.equals(n.value)) {
                remove(n);
                numMods++;
                return true;
            }
        }
        return false;
    }

    /**
       Returns <tt>true</tt> if this list contains the specified element. 
       More formally, returns true if and only if this list contains
       atleast one element <tt>e</tt> such that <tt>(o == null ? e == null : o.equals(e))</tt>.
    */
    public final boolean contains(Object o)
    {
        for (T e : this)
            if (o == null ? e == null : o.equals(e))
                return true;
        return false;
    }

    /**
       Clear the list so that it is empty.
     */
    public final void clear()
    {
        numMods++;
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            n.list = null;
        }
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }


    /**
       Returns an array containing all the elements in the correct order.
       
       @param a an array in which to place the elements.
       If <tt>a</tt> is not large enough, a new array is allocated and returned.
     */
    @SuppressWarnings("unchecked")
    public <V> V[] toArray(V[] a)
    {
        if (a.length < size) {
            a = (V[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        }
        Node<T> n = sentinel.next;
        int i=0;
        while(n != sentinel) 
            a[i++] = (V) n.value;
        return a;
    }

    /**
       Produce an Object[] of values.
     */
    public Object[] toArray()
    {
        Object[] result = new Object[size];
        int i = 0;
        for (T t : this)
            result[i++] = t;
        return result;
    }

    /**
       Return true iff this list is empty.
     */
    public final boolean isEmpty()
    {
        return size == 0;
    }

    /**
       Produce a hashcode for this list.

       This uses the hashcode of a fixed sentinel node.
     */
    public final int hashCode()
    {
        return sentinel.hashCode();
    }

    /**
       Return a copy of this list from <tt>fromIndex</tt> inclusive
       to <tt>toIndex</tt> exclusive.
     */
    public final List<T> subList(int fromIndex, int toIndex)
    {
        int i=0;
        List<T> result = new CList<T>();
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            if (i >= fromIndex) {
                if (i < toIndex) {
                    result.add(n.value);
                } else {
                    break;
                }
            }
        }
        return result;
    }

    /**
       Return the first index of the object <tt>o</tt>.
     */
    public final int indexOf(Object o)
    {
        int i=0;
        for (T v : this) {
            if (o == null ? v == null : o.equals(v)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
       Return the last index of the object <tt>o</tt> in this list, or
       <tt>-1</tT> if o is not in this list.
       Equality is checked with an element <tt>e</tt> as <tt>(o == null ? e==null : o.equals(e))</tt>
     */
    public final int lastIndexOf(Object o)
    {
        int i=0,j=-1;
        for (T v : this) {
            if (o == null ? v == null : o.equals(v)) {
                j=i;
            }
            i++;
        }
        return j;
    }

    /**
       Returns the head of the list, or <tt>null</tt> if the list is empty.
     */
    public final T peek()
    {
        return size > 0 ? getFirst() : null;
    }

    /**
       Returns the head of the list.
       @throws NoSuchElementException if the list is empty.
     */
    public final T element()
    {
        return getFirst();
    }

    /**
       Returns the head of the list, or <tt>null</tt> if the list is empty.
     */
    public final T poll()
    {
        numMods++;
        return size > 0 ? removeFirst() : null;
    }

    /**
       Retrieves and removes the head of this list.
       @throws NoSuchElementException if the list is empty.
     */
    public final T remove()
    {
        return removeFirst();
    }

    /**
       Adds the specified element to the tail of the list.
     */
    public final boolean offer(T o)
    {
        append(o);
        return true;
    }

    /**
       Remove an element at a specified position.

       @param i the index of the element to remove
       @return the element at position <tt>i</tt>
       @throws IndexOutOfBoundsException if <tt>i&lt;0 || i&gt;=size()</tt>.
     */
    public final T remove(int i)
    {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        numMods++;
        int j = 0;
        T res=null;
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            if (j++ == i) {
                res = n.value;
                remove(n);
                break;
            }
        }
        return res;
    }

    /**
       Add an element at a specified position, shifting the positions
       of everything to the right by one.

       @param i the position of the element to be added
       @param v the element to add
       @throws IndexOutOfBoundsException if <tt>i&lt;0 || i&gt;=size()</tt>.
     */
    public final void add(int i, T v)
    {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException();
        numMods++;
        int j=0;
        Node<T> node = new Node<T>(this, v);
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            if (j++ == i) {
                node.prev = n.prev;
                node.next = n;
                n.prev.next = node;
                n.prev = node;
                size++;
                break;
            }
        }
    }

    /**
       Set the value of an element at a specified index.

       @param i the position of the element to be set.
       @param v the new element value.
       @throws IndexOutOfBoundsException if <tt>i&lt;0 || i&gt;=size()</tt>.
     */
    public final T set(int i, T v)
    {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException();
        numMods++;
        int j=0;
        T res=null;
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            if (j++ == i) {
                res = n.value;
                n.value = v;
                break;
            }
        }
        return res;
    }

    /**
       Unsupported operation
     */
    public final boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    /**
       Unsupported operation
     */
    public final boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    /**
       Create a human readable string (human readable if the list is small enough).
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("stixar.util.CList[");
        for (T elt : this) {
            sb.append(elt + " ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Serialize this list.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
	s.defaultWriteObject();
        s.writeInt(size);
        for (Node<T> n = sentinel.next; n != sentinel; n = n.next) {
            s.writeObject(n.value);
        }
    }

    /**
     * Deserialize this list.
     */
    @SuppressWarnings(value="unchecked")
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
	s.defaultReadObject();
        int size = s.readInt();
        sentinel = new Node<T>(this, null);
        sentinel.next = sentinel.prev = sentinel;
	for (int i=0; i<size; i++)
            append((T)s.readObject());
    }

    public static <T> ListCell<T> nullCell()
    {
        return new ListCell<T>() {
            public T value() { return null; }
            public T value(T v) { return null; }
            public boolean isValid() { return false; }
            public ListCell<T> next() { return null; }
            public ListCell<T> prev() { return null; }
        };
    }

    /**
       A node belonging in an openlist.
     */
    protected static class Node<T> implements ListCell<T>
    {
        Node<T> next;
        Node<T> prev;
        CList<T> list;
        public T value;
        
        Node(CList<T> list, T value)
        {
            this.value = value;
            this.next = null;
            this.prev = null;
            this.list = list;
        }

        public final ListCell<T> next()
        {
            return next == list.sentinel ? null : next;
        }

        public final ListCell<T> prev()
        {
            return prev == list.sentinel ? null : prev;
        }

        public final T value()
        {
            return value;
        }

        public final T value(T v)
        {
            return value = v;
        }

        public final boolean isValid()
        {
            return list != null && this != list.sentinel;
        }

        public String toString()
        {
            return String.format("ListCell[%s]", value);
        }
    }


    protected static class NodeIterator<T> 
        implements ListIterator<T>
    {
        
        protected Node<T> node;
        protected int pos;
        protected int expectedMods;
        protected boolean forward;
        
        public NodeIterator(Node<T> node, CList<T> list)
        {
            this.node = node;
            this.pos = 0;
            this.forward = true;
            this.expectedMods = list.numMods;
        }
        
        public final boolean hasNext() 
        {
            return node.list != null && node != node.list.sentinel;
        }

        public final boolean hasPrevious()
        {
            return node.list != null && node != node.list.sentinel;
        }

        public final void set(T v)
        {
            if (expectedMods != node.list.numMods && 
                (expectedMods > 0 && node.list.numMods > 0)) {
                throw new ConcurrentModificationException();
            }
            expectedMods++; 
            node.list.numMods++;

            if (forward) {
                if (node.prev == node.list.sentinel) {
                    throw new IllegalStateException();
                }
                node.prev.value = v;
            } else {
                if (node.next == node.list.sentinel) {
                    throw new IllegalStateException();
                }
                node.next.value = v;
            }
        }

        public final T next()
        {
            if (expectedMods != node.list.numMods && 
                (expectedMods > 0 && node.list.numMods > 0)) {
                throw new ConcurrentModificationException();
            }
            T result = node.value;
            node = node.next;
            pos++;
            forward = true;
            return result;
        }

        public final T previous()
        {
            if (expectedMods != node.list.numMods && 
                (expectedMods > 0 && node.list.numMods > 0)) {
                throw new ConcurrentModificationException();
            }
            T result = node.value;
            node = node.prev;
            pos--;
            forward = false;
            return result;
        }
        
        public final void remove()
        {
            if (node == node.list.sentinel) {
                throw new IllegalStateException();
            }
            if (expectedMods != node.list.numMods && 
                (expectedMods > 0 && node.list.numMods > 0)) {
                throw new ConcurrentModificationException();
            }
            expectedMods++; 
            node.list.numMods++;
            if (forward) {
                node.list.remove(node.prev);
            } else {
                node.list.remove(node.next);
            }
        }

        public final void add(T v)
        {
            if (expectedMods != node.list.numMods && 
                (expectedMods > 0 && node.list.numMods > 0)) {
                throw new ConcurrentModificationException();
            }
            expectedMods++; 
            node.list.numMods++;
            Node<T> n = new Node<T>(node.list, v);
            n.prev = node.prev;
            node.prev.next = n;
            n.next = node;
            node.prev = n;
            node.list.size++;
            pos++;
        }

        public final int nextIndex()
        {
            return pos;
        }

        public final int previousIndex()
        {
            return pos - 1;
        }
    }
}
