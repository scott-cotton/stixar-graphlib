package stixar.util.fheap;

import java.util.LinkedList;
import java.util.Iterator;

class FHIterator<E> implements Iterator<E>
{
    protected LinkedList<FHeapNode<E>> cycleQueue;
    protected FHeapNode<E> current;
    protected FHeapNode<E> target;
    protected boolean targetDone;

    public FHIterator(FHeapNode<E> root)
    {
        this.target = root;
        this.current = root;
        this.targetDone = false;
        this.cycleQueue = new LinkedList<FHeapNode<E>>();
        if (root != null && root.child != null) {
            cycleQueue.addLast(root.child);
        }
    }

    public boolean hasNext()
    {
        if (!cycleQueue.isEmpty()) return true;
        if (current == null) return false;
        if (current == target) {
            return !targetDone;
        }
        return true;
    }

    public E next()
    {
        E result = current.value();
        if (current == target) {
            targetDone = true;
        }
        current = current.right;
        if (current == target) {
            assert targetDone;
            if (!cycleQueue.isEmpty()) {
                current = target = cycleQueue.removeFirst();
                targetDone = false;
            }
        }
        if (current.child != null) {
            cycleQueue.addLast(current.child);
        }
        return result;
    }

    /**
       @throw UnsupportedOperationException.
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
