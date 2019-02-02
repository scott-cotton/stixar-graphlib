package stixar.util.fheap;

import stixar.util.PQueue;
import stixar.util.Cell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import java.util.Arrays;

/**
   A Fibonacci heap.
 */
public class FibHeap<E> implements PQueue<E>
{
    protected Comparator<? super E> cmp;
    protected int size;
    protected E maxElt;
    protected FHeapNode<E> minKey;

    protected static final double phi = (1d + Math.sqrt(5d)) / 2d;

    protected ArrayList<FHeapNode<E>> todo;
    protected Object[] degrees;

    private static int[] _dSizes = new int[40];
    static {
        for (int i=0; i<40; ++i) {
            _dSizes[i] = (int) Math.pow(phi, i);
        }
    }
    static final int[] dSizes = _dSizes;


    /**
       Create a Fibonacci heap with comparator <tt>cmp</tt>

       @param cmp the comparator with which measure priorities.
     */
    public FibHeap(Comparator<? super E> cmp)
    {
        todo = new ArrayList<FHeapNode<E>>();
        degrees = new Object[40];
        minKey = null;
        maxElt = null;
        size = 0;
        this.cmp = cmp;
    }

    /**
       Return the number of items in this heap.

       @return the number of items in this heap.
     */
    public final int size() 
    {
        return size;
    }

    /**
       Return whether this heap is empty.

       @return true iff this heap is empty
     */
    public final boolean isEmpty()
    {
        return size == 0;
    }

    /**
       Insert an element into the heap with priority determined by
       this heaps comparator.

       @param elt the element to insert.
     */
    public FHeapCell<E> insert(E elt)
    {
        FHeapNode<E> node = new FHeapNode<E>(elt);
        if (minKey != null) {
            add(node, minKey);
            if (cmp.compare(node.value(), minKey.value()) < 0)
                minKey = node;
        } else {
            minKey = node;
        }
        size++;
        return node;
    }

    /**
       extract the minimum value from the heap.
       Return <tt>null</tt> if the queue is empty.
    */
    public final E extractMin()
    {
        if (minKey == null) return null;
        E result = minKey.value();
        minKey.valid(false);
        if (minKey.child != null) {
            FHeapNode<E> c = minKey.child;
            FHeapNode<E> t = c;
            do {
                FHeapNode<E> next = c.right;
                add(c, minKey);
                c.parent = null;
                c = next;
            } while(c != t);
        }
        if (minKey == minKey.right) {
            minKey = null;
        } else {
            FHeapNode<E> tmp = minKey.right;
            rm(minKey);
            minKey = tmp;
            consolidate();
        }
        size--;
        return result;
    }

    /**
       Return the minimum item in the queue, or 
       <tt>null</tt> if the queue is empty.
     */
    public final E min()
    {
        if (minKey == null) return null;
        return minKey.value();
    }

    /**
       Increase the priority (decrease the value of the priority measure) of 
       an item in the heap.
       @param item the element whose priority is increased.
       
       This operation is undefined if called on an item whose priority has decreased
       (value of priority measure increased).

       @throws ClassCastException if <tt>item</tt> is not an instance
       of {@link FHeapCell}
    */
    @SuppressWarnings(value={"unchecked"})
    public void increasePriority(Cell<E> item)
    {
        if (item == null) 
            throw new IllegalArgumentException("null FHeapCell");
        FHeapNode<E> node = (FHeapNode<E>) item;
        if (node.parent != null) {
            if (cmp.compare(node.value(), node.parent.value()) < 0) {
                FHeapNode<E> parent = node.parent;
                cut(node, parent);
                cascade(parent);
            }
        }
        if (cmp.compare(node.value(), minKey.value()) < 0) {
            minKey = node;
        }
    }
    
    public void requeue(Cell<E> item)
    {
        increasePriority(item);
    }

    /**
       Set the maximum possible priority element to <tt>elt</tt>.  This element
       is not stored in the heap, but used to {@link #remove} elements
       by temporarily replacing elements to remove with <tt>elt</tt>  and then
       calling {@link #extractMin}.
     */
    public void maxElt(E elt)
    {
        maxElt = elt;
    }

    /**
       Removes an element from the heap.

       @throws NullPointerException if the maximum possible element has 
       not been set via {@link #maxElt}.
       
       @see #maxElt
     */
    public void remove(FHeapCell<E> cell)
    {
        if (maxElt == null) {
            throw new IllegalStateException("must have set maxElt() to remove an item.");
        }
        FHeapNode<E> node = (FHeapNode<E>) cell;
        E orgElt = node.value();
        node.value(maxElt);
        increasePriority(node);
        E minElt = extractMin();
        assert minElt == maxElt;
        node.value(orgElt);
    }

    /**
       Make FibHeap's iterable.
     */
    public Iterator<E> iterator()
    {
        return new FHIterator<E>(minKey);
    }


    protected final int maxDegree()
    {
        int idx = Arrays.binarySearch(dSizes, size);
        if (idx < 0) return -idx;
        return idx;
    }

    public void clear()
    {
        todo = new ArrayList<FHeapNode<E>>();
        Arrays.fill(degrees, null);
        minKey = null;
        maxElt = null;
        size = 0;
    }


    /*
      This method causes javac warning because of use of generic arrays
      (in the variable degrees).
      It is significantly faster (2x) than using fully type safe lists,
      so we decide to accept the compiler warnings for jdk < 1.6.0.
     */
    @SuppressWarnings(value={"unchecked"})
    protected final void consolidate()
    {
        todo.clear();
        int dlen = maxDegree();
        Arrays.fill(degrees, 0, dlen, null);
        for (FHeapNode<E> t = minKey; ; t = t.right) {
            todo.add(t);
            if (t.right == minKey)
                break;
        }
        for (FHeapNode<E> c : todo) {
            int degree = c.degree;
            while(degrees[degree] != null) {
                FHeapNode<E> y = (FHeapNode<E>) degrees[degree];
                if (cmp.compare(y.value(), c.value()) < 0) {
                    FHeapNode<E> tmp = y;
                    y = c;
                    c = tmp;
                }
                link(y, c);
                degrees[degree] = null;
                degree++;
                if (degree > dlen) {
                    throw new IllegalStateException("expected max degree " + dlen
                                                    + " but found " + degree);
                }
            }
            degrees[degree] = c;
        }
        minKey = null;
        for (int i=0; i<dlen; ++i) {
            FHeapNode<E> node = (FHeapNode<E>) degrees[i];
            if (node != null) {
                if (minKey == null) {
                    minKey = node;
                    minKey.left = minKey;
                    minKey.right = minKey;
                } else {
                    add(node, minKey);
                }
                if (cmp.compare(node.value(), minKey.value()) < 0) {
                    minKey = node;
                }
            }
        }
    }

    protected final void add(FHeapNode<E> elt, FHeapNode<E> lst)
    {
        FHeapNode<E> tmp = lst.left;
        elt.right = lst;
        lst.left = elt;
        elt.left = tmp;
        tmp.right = elt;
    }

    protected final void concat(FHeapNode<E> l1, FHeapNode<E> l2)
    {
        FHeapNode<E> l1Start = l1.right;
        l1.right = l2;
        FHeapNode<E> l2End = l2.left;
        l2.left = l1;
        l1Start.left = l2End;
        l2End.right = l1Start;
    }

    protected final void rm(FHeapNode<E> elt)
    {
        if (elt == elt.right) return;
        elt.right.left = elt.left;
        elt.left.right = elt.right;
        elt.right = elt;
        elt.left = elt;
    }

    protected final void link(FHeapNode<E> y, FHeapNode<E> x)
    {
        rm(y);
        y.parent = x;
        if (x.child == null) {
            x.child = y;
        } else {
            add(y, x.child);
        }
        x.degree++;
        y.mark = false;
    }

    protected final void cut(FHeapNode<E> c, FHeapNode<E> p)
    {
        if (c == p.child) p.child = c.right;
        if (c == p.child) p.child = null;
        rm(c);
        add(c, minKey);
        p.degree--;
        c.parent = null;
        c.mark = false;
    }

    protected final void cascade(FHeapNode<E> node)
    {
        if (node.parent != null) {
            if (!node.mark) {
                node.mark = true;
            } else {
                FHeapNode<E> p = node.parent;
                cut(node, p);
                cascade(p);
            }
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for(FHeapNode<E> n=minKey; ; n = n.right) {
            format(n, sb, 0);
            if (n.right == minKey) break;
        }
        return sb.toString();
    }

    protected void format(FHeapNode<E> node, StringBuffer sb, int lvl) 
    {
        for (int i=0; i< lvl; ++i) sb.append(" ");
        if (node == minKey) {
            sb.append("*" + node.toString() + "\n");
        } else {
            sb.append(node.toString() + "\n");
        }
        if (node.child != null) {
            for (FHeapNode<E> child = node.child; ; child = child.right) {
                format(child, sb, lvl+1);
                if (child.right == node.child) break;
            }
        }
    }
}
