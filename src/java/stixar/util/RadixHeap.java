package stixar.util;

import java.util.BitSet;
import java.util.ArrayList;
import java.util.Iterator;

public class RadixHeap<T> implements PQueue<T>
{

    public interface LongValuator<T>
    {
        public long longValue(T v);
    }

    public static byte maxBits(long maxValue)
    {
        int v = 1;
        int i = 1;
        for ( i=1; v < maxValue; ++i) {
            v *= 2;
        }
        return (byte) i;
    }

    public static byte maxBits(long maxValue, byte chunkSize)
    {
        int v = 1;
        int i = 1;
        for ( i=1; v < maxValue; ++i) {
            v *= 2;
        }
        if (chunkSize > i) {
            throw new IllegalArgumentException("chunkSize to big for maxValue");
        }
        while(i % chunkSize != 0) i++;
        return (byte) i;
    }

    private byte maxBits;
    private byte chunkSize;
    private int size;
    private LongValuator<T> longValuator;
    private HeapNode<T> topNode, bottomNode;
    private HeapNode[] heapNodes;
    private long lastLB;
    private int noH, noM, noL;


    public RadixHeap(LongValuator<T> lv, byte maxBits, byte chunkSize)
    {
        if (maxBits % chunkSize != 0) {
            throw new IllegalArgumentException();
        }
        this.maxBits = maxBits;
        this.longValuator = lv;
        this.chunkSize = chunkSize;
        this.heapNodes = new HeapNode[maxBits / chunkSize];
        HeapNode<T> child = null;
        for (int i=0; i*chunkSize < maxBits - 1; ++i) {
            HeapNode<T> node = new HeapNode<T>(lv, chunkSize, (byte) i, child);
            heapNodes[i] = node;
            child = node;
            if (bottomNode == null) 
                this.bottomNode = node;
            this.topNode = node;
        }
        this.size = 0;
        this.lastLB = 0L;
    }

    public LongValuator<T> longValuator(LongValuator<T> lv)
    {
        LongValuator<T> result = this.longValuator;
        this.longValuator = lv;
        for (HeapNode<T> n = topNode; n != null; n = n.child) {
            n.setLongValuator(lv);
        }
        return result;
    }

    public LongValuator<T> longValuator()
    {
        return this.longValuator;
    }

    /**
       Insert an element into the priority queue.
       @param elt the element to insert
       @return a Cell which gives a handle on the element's position
       in the priority queue.
     */
    public final Cell<T> insert(T elt)
    {
        long v = longValuator.longValue(elt);
        assert v >= 0L;
        HeapNode<T> hNode = topNode;
        while((int) ((v & hNode.mask) >>> hNode.shift) == hNode.minIndex) {
            if (hNode.child == null) break;
            hNode = hNode.child;
        }
        //HeapNode<T> hNode = nodeOf(v);
        RHCell<T> result = hNode.insert(elt, v);
        //System.out.printf("inserted %d into %s at index %d\n", v, hNode, result.bucket);
        size++;
        return result;
    }


    /**
       Extract the highest priority element, or element with the least
       value as determined by the priority queues comparator.  Return
       <tt>null</tt> if the queue is empty.
     */
    public T extractMin()
    {
        if (size == 0) return null;
        if (bottomNode.size == 0) {
            step();
        }
        RHCell<T> resCell = bottomNode.extractMin();
        resCell.node = null;
        size--;
        if (size > 0) {
            lastLB = resCell.longValue;
        } else {
            lastLB = 0L;
            for (HeapNode<T> n = topNode; n != null; n = n.child) {
                assert n.size == 0;
                n.minIndex = 0;
            }
        }
        //System.out.println("now bottomNode is " + bottomNode);
        return resCell.value();
    }

    /**
       return the minimum valued item in the queue, or <tt>null</tt> if
       the queue is empty.
     */
    public T min()
    {
        if (size == 0) return null;
        if (bottomNode.size == 0) {
            step();
        }
        RHCell<T> resCell = bottomNode.min();
        lastLB = resCell.longValue;
        return resCell.value();
    }

    public long minValue()
    {
        if (size == 0) throw new IllegalStateException();
        if (bottomNode.size == 0)
            step();
        RHCell<T> resCell = bottomNode.min();
        return resCell.longValue;
    }

    /**
       Increase the priority of the element contained in a cell.
       @param cell the cell containing the element whose priority
       is to be increased.
     */
    @SuppressWarnings("unchecked")
    public void requeue(Cell<T> cell)
    {
        RHCell<T> rhCell = (RHCell<T>) cell;
        //System.out.println("requeue " + rhCell);
        long lv = longValuator.longValue(rhCell.value);
        //System.out.printf("old value %d new value %d\n", rhCell.longValue, lv);
        HeapNode<T> hNode = topNode;
        while((int) ((lv & hNode.mask) >>> hNode.shift) == hNode.minIndex) {
            if (hNode.child == null) break;
            hNode = hNode.child;
        }
        //HeapNode<T> hNode = nodeOf(lv);
        rhCell.node.remove(rhCell);
        hNode.insertOld(rhCell, lv);
    }

    @SuppressWarnings("unchecked")
    public void remove(Cell<T> cell)
    {
        RHCell<T> rhCell = (RHCell<T>) cell;
        rhCell.node.remove(rhCell);
        size--;
    }

    /**
       Clear the priority queue so that it contains no items.
     */
    public void clear()
    {
        for (HeapNode<T> node = topNode; node != null; node = node.child) {
            node.clear();
        }
        size = 0;
    }

    /**
       Return whether or not the priority queue is empty.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
       Return the size of the priority queue.
     */
    public int size()
    {
        return size;
    }

    public Iterator<T> iterator()
    {
        return null;
    }

    private HeapNode<T> nodeOf(long v)
    {
        HeapNode<T> result = topNode;
        while((int) ((v & result.mask) >>> result.shift) == result.minIndex) {
            if (result.child == null) break;
            result = result.child;
        }
        return result;
    }


    private void step()
    {
        //System.out.println("step");
        assert size > 0 && bottomNode.size == 0;
        HeapNode<T> leastNonEmpty = bottomNode;
        while(leastNonEmpty.size == 0) {
            //System.out.printf("empty: %s\n", leastNonEmpty);
            leastNonEmpty = leastNonEmpty.parent;
        }
        //System.out.println("least non empty: " + leastNonEmpty);
        int omi = leastNonEmpty.minIndex;
        int nmi = leastNonEmpty.minIndex = leastNonEmpty.active.nextSetBit(omi + 1);
        leastNonEmpty.active.set(nmi, false);
        CList<RHCell<T>> minBucket = (CList<RHCell<T>>) leastNonEmpty.buckets[nmi];
        //System.out.println("minBucket size: " + minBucket.size());
        leastNonEmpty.size -= minBucket.size();
        HeapNode<T> toFill = leastNonEmpty.child;
        while(toFill != null) {
            //System.out.println("filling " + toFill);
            assert toFill.size == 0;
            int minIndex = Integer.MAX_VALUE;
            toFill.minIndex = -1;
            while(!minBucket.isEmpty()) {
                RHCell<T> rhCell  = minBucket.removeFirst();
                toFill.insertOld(rhCell, rhCell.longValue);
                if (rhCell.bucket < minIndex) {
                    minIndex = rhCell.bucket;
                }
            }
            minBucket = (CList<RHCell<T>>) toFill.buckets[minIndex];
            toFill.minIndex = minIndex;

            if (toFill.child != null) {
                toFill.size -= minBucket.size();
                toFill.active.set(minIndex, false);
            } 
            toFill = toFill.child;
        }
    }


    private class HeapNode<T>
    {
        long mask;
        int shift;
        int minIndex;
        Object[] buckets;
        BitSet active;
        HeapNode<T> parent;
        HeapNode<T> child;
        int size;
        LongValuator<T> longValuator;


        HeapNode(LongValuator<T> lv, byte chunkSize, byte offset, HeapNode<T> child)
        {
            int numBuckets = 1 << chunkSize;
            this.buckets = new Object[numBuckets];
            for (int i=0; i < numBuckets; ++i) 
                buckets[i] = new CList<RHCell<T>>();
            this.minIndex = 0;
            this.mask = 0L;

            shift = offset * chunkSize;
            for (int i=0; i<chunkSize; ++i) {
                mask |= 1L << (i + shift);
            }
            this.child = child;
            if (child != null) {
                child.parent = this;
            }
            this.size = 0;
            this.longValuator = lv;
            this.active = new BitSet(numBuckets);
        }

        public String toString()
        {
            return String.format("HeapNode mask=%d shift=%d minIndex=%d size=%d", mask, shift, minIndex, size);
        }

        void setLongValuator(LongValuator<T> lv)
        {
            this.longValuator = lv;
        }
        
        RHCell<T> min()
        {
            assert size > 0 && child == null;
            CList<RHCell<T>> minList = (CList<RHCell<T>>) buckets[minIndex];
            if (minList.isEmpty()) {
                minIndex = active.nextSetBit(minIndex + 1);
                minList = (CList<RHCell<T>>) buckets[minIndex];
            }
            RHCell<T> result = minList.getFirst();
            return result;
        }

        RHCell<T> extractMin()
        {
            assert size > 0 && child == null;
            size--;
            CList<RHCell<T>> minList = (CList<RHCell<T>>) buckets[minIndex];
            if (minList.isEmpty()) {
                minIndex = active.nextSetBit(minIndex + 1);
                minList = (CList<RHCell<T>>) buckets[minIndex];
            }
            RHCell<T> result = minList.removeFirst();
            if (minList.isEmpty()) {
                active.set(minIndex, false);
                if (size == 0) {
                    minIndex = 0;
                } 
            }
            result.node = null;
            return result;
        }

        int indexOf(T value)
        {
            long v = longValuator.longValue(value) & mask;
            return (int) ((v & mask) >>> shift);
        }

        final RHCell<T> insert(T value, long v)
        {
            size++;
            int i = (int) ((v & mask) >>> shift);
            assert v >= 0L;
            assert child == null || i > minIndex : String.format("bad insert at index %d of value %d in %s",
                                                                 i, v, this);
            CList<RHCell<T>> tList = (CList<RHCell<T>>) buckets[i];
            if (tList.isEmpty()) {
                active.set(i, true);
            }
            RHCell<T> rhCell = new RHCell<T>(value, v, this, i);
            rhCell.bCell = tList.append(rhCell);
            return rhCell;
        }

        final RHCell<T> insertOld(RHCell<T> oldCell, long lv)
        {
            size++;
            int bucket = (int) ((lv & mask) >>> shift);
            assert child == null || bucket > minIndex : String.format("%s bucket %d", this, bucket);
            oldCell.node = this;
            oldCell.bucket = bucket;
            oldCell.longValue = lv;
            CList<RHCell<T>> tList = (CList<RHCell<T>>) buckets[bucket];
            if (tList.isEmpty()) {
                active.set(bucket, true);
            }
            oldCell.bCell = tList.append(oldCell);
            return oldCell;
        }

        final void remove(RHCell<T> cell)
        {
            size--;
            CList<RHCell<T>> tList = (CList<RHCell<T>>) buckets[cell.bucket];
            tList.remove(cell.bCell);
            if (tList.isEmpty()) {
                active.set(cell.bucket, false);
            }
            cell.node = null;
        }

        void clear()
        {
            int ttl = 0;
            for (int i = active.nextSetBit(0); i != -1; i = active.nextSetBit(i + 1)) {
                CList<RHCell<T>> tList = (CList<RHCell<T>>) buckets[i];
                for (RHCell<T> rhc : tList) {
                    rhc.node = null;
                }
                ttl += tList.size();
                assert tList.size() > 0;
                tList.clear();
            }
            active.clear();
            CList<RHCell<T>> tList = (CList<RHCell<T>>) buckets[minIndex];
            if (tList.size() > 0) {
                throw new IllegalStateException(toString());
            }
            assert size == ttl : this;
            minIndex = 0;
            size = 0;
        }
    }

    private class RHCell<T> implements Cell<T>
    {
        HeapNode<T> node;
        int bucket;
        ListCell<RHCell<T>> bCell;
        T value;
        long longValue;

        RHCell(T v, long lv, HeapNode<T> hNode, int bucket)
        {
            this.value = v;
            this.longValue = lv;
            this.node = hNode;
            this.bucket = bucket;
            this.bCell = null;
        }

        public T value() 
        { 
            return value;
        }

        public T value(T v)
        {
            return value = v;
        }

        public boolean isValid() 
        {
            return node != null;
        }

        public String toString()
        {
            return String.format("RHCell[value: %s; lv: %d; node: %s; bucket: %d]",
                                 value, longValue, node, bucket);
        }
    }
}
