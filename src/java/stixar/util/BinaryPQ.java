package stixar.util;

import java.util.Comparator;
import java.util.Collection;
import java.util.Iterator;

/**
   An array backed binary heap priority queue.
   <p>
   This class adapts a binary heap to the {@link PQueue} interface.
   As such, it supports requeueing of objects by using {@link Cell cells}.
   </p><p>
   This class is similar to the java <tt>PriorityQueue</tt>, however the implementation
   maintains the cells for objects in the queue along with the objects themselves. 
   Consequently, if re-queueing is not required, then we recomend using the <tt>PriorityQueue</tt> 
   interface.
   </p>
 */
public class BinaryPQ<E>
    implements PQueue<E>
{
    protected Object[] elements;
    protected BCell[] cells;

    protected int size;
    protected final Comparator<? super E> cmp;

    protected static class BCell
    {
        int index;
        BCell(int idx) { index = idx; }
    }

    protected class BPQCell<E> extends BCell implements Cell<E>
    {
        BPQCell(int idx)
        {
            super(idx);
        }

        public boolean isValid()
        {
            return index != -1;
        }

        @SuppressWarnings("unchecked")
        public E value()
        {
            return (E) elements[index];
        }

        public String toString()
        {
            return String.format("BinaryPQCell@%d", index);
        }
    }

    /**
       Construct a binary priority queue with a comparator.
     */
    public BinaryPQ(Comparator<? super E> cmp)
    {
        this(cmp, 11);
    }

    /**
       Construct a binary priority queue with a comparator
       specifying an initial capacity.
     */
    @SuppressWarnings("unchecked")
    public BinaryPQ(Comparator<? super E> cmp, int capacity)
    {
        elements = new Object[capacity + 1];
        cells = new BCell[capacity + 1];
        this.cmp = cmp;
        size = 0;
    }


    /**
       Construct a binary priority from a collection
       specifying a comparator.
       <p>
       The elements in the collection are put into a heap
       datastructure in linear time, using the standard 
       heapification method.
       </p>
     */
    public BinaryPQ(Collection<E> collection, Comparator<? super E> cmp)
    {
        elements = new BCell[collection.size() + 1];
        int i=0;
        for (E elt: collection) {
            elements[i] = elt;
            cells[i] = new BPQCell<E>(i);
            ++i;
        }
        this.cmp = cmp;
        heapify();
    }

    /**
       Insert an element into the priority queue.
       <p>
       The new element is placed into the queue with priority determined 
       by the comparator with which this queue was created.
       </p>
       @param elt the element to insert into the priority queue
       @return A cell for the element which can be used for requeueing.
     */
    public Cell<E> insert(E elt)
    {
        if (size == elements.length - 1) {
            Object[] ca = new Object[elements.length * 2];
            System.arraycopy(elements, 0, ca, 0, elements.length);
            elements = ca;
            BCell[] ci = new BCell[cells.length * 2];
            System.arraycopy(cells, 0, ci, 0, cells.length);
            cells = ci;
        }
        size++;
        BPQCell<E> result = new BPQCell<E>(size);
        elements[size] = elt;
        cells[size] = result;
        shiftUp(size);
        return result;
    }

    /**
       Remove and return the highest priority element, which is taken to be the least 
       element according to this priority queues comparator.
       
       @return the highest priority element, which is taken to be the least 
       element according to this priority queues comparator.
     */
    @SuppressWarnings("unchecked")
    public E extractMin()
    {
        if (size == 0) return null;
        E result = (E) elements[1];
        cells[1].index = -1;
        cells[1] = cells[size];
        if (size > 1)
            cells[1].index = 1;
        cells[size] = null;
        elements[1] = elements[size];
        elements[size--] = null;
        if (size > 1) shiftDown(1);
        return result;
    }

    /**
       Return the highest priority element, which is taken to be the least 
       element according to this priority queues comparator.  The element
       remains in the queue.
       
       @return the highest priority element, which is taken to be the least 
       element according to this priority queues comparator.
     */
    @SuppressWarnings("unchecked")
    public E min()
    {
        if (size == 0) return null;
        return (E) elements[1];
    }

    /**
       Requeue an element in the queue.  If the priority for the element 
       has changed since it was last requeued (or inserted), this operation 
       will adjust the priority queue according to the new priority.  
       <p>This priority queue is not monotonic: it supports increases as well as 
       decreases in priority.
       </p>
       @param cell the cell of the element to requeue, which was returned 
       when the element was {@link #insert inserted}.
     */
    public void requeue(Cell<E> cell)
    {
        BCell c = (BCell) cell;
        if (c.index == -1) 
            throw new IllegalArgumentException();
        int i = c.index;
        if (i > 1) {
            shiftUp(i);
        } 
        if (i <= (size << 1)) {
            shiftDown(i);
        }
    }


    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public void clear()
    {
        for (int i=1; i<= size; ++i) {
            elements[i] = null;
            cells[i].index = -1;
            cells[i] = null;
        }
        size = 0;
    }

    
    public Iterator<E> iterator()
    {
        return new heapiter();
    }

    private void heapify()
    {
        for (int i = size/2; i >= 1; i--)
            shiftDown(i);
    }

    @SuppressWarnings("unchecked")
    private void shiftDown(int k)
    {
        int j;
        while ((j = k << 1) <= size && (j > 0)) {
            if (j<size &&
                cmp.compare((E) elements[j], (E) elements[j+1]) > 0) {
                j++;
            }
            if (cmp.compare((E) elements[k], (E) elements[j]) <= 0)
                break;
            Object tmp = elements[j];
            elements[j] = elements[k];
            elements[k] = tmp;
            BCell c = cells[j];
            cells[j] = cells[k]; cells[j].index = j;
            cells[k] = c; c.index = k;
            k = j;
        }
    }

    @SuppressWarnings("unchecked")
    private void shiftUp(int k)
    {
        while (k > 1) {
            int j = k >>> 1;
            if (cmp.compare((E) elements[j], (E) elements[k]) <= 0)
                break;
            Object tmp = elements[j];
            elements[j] = elements[k];
            elements[k] = tmp;
            BCell c = cells[j];
            cells[j] = cells[k]; cells[j].index = j;
            cells[k] = c; c.index = k;
            k = j;
        }
    }

    private class heapiter implements Iterator<E>
    {
        private int index;
        heapiter()
        {
            index = 1;
        }

        public boolean hasNext()
        {
            return index <= size;
        }

        @SuppressWarnings("unchecked")
        public E next()
        {
            return (E) elements[index++];
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
