package stixar.util.fheap;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;

import java.util.PriorityQueue;

class Elt
{
    public Elt(int v) { value = v; }
    public int value;
    public String toString() { return (new Integer(value)).toString(); }
}


class FHC  implements Comparator<Elt>
{
    public FHC() {}

    public final int compare(final Elt e1, final Elt e2)
    {
        return e1.value < e2.value ? -1 : (e1.value == e2.value ? 0 : 1);
    }
}


public class FibHeapTest extends TestCase
{
    public FibHeapTest() { super(); }

    protected static final int heapSize = 200000;

    public void testFibHeap()
    {
        FibHeap<Elt> fh = new FibHeap<Elt>(new FHC());
        ArrayList<FHeapCell<Elt>> items = new ArrayList<FHeapCell<Elt>>(heapSize);
        for (int i=0; i<heapSize; ++i) {
            items.add(fh.insert(new Elt(-i)));

        }
        System.out.println("inserted");
        long fhStart = System.currentTimeMillis();
        for (int i=0; i<heapSize * 50; ++i) {
            FHeapCell<Elt> item = items.get(i % heapSize);
            item.value().value--;
            fh.increasePriority(item);
        } 
        long fhEnd = System.currentTimeMillis();        
        System.out.println("Fib heap increased priorities 50x in " + (fhEnd  - fhStart) + " millis ");
        System.out.println("increased");
        //System.out.println(fh);
        fhStart = System.currentTimeMillis();
        while(!fh.isEmpty()) {
            Elt e = fh.extractMin();
            //System.out.println(e);
            //System.out.println(fh);
        }
        fhEnd = System.currentTimeMillis();
        System.out.println("Fib heap removed all items in " + (fhEnd  - fhStart) + " millis ");
        
    }

    public void testRemove()
    {
        int rhs = 10;
        FibHeap<Elt> fh = new FibHeap<Elt>(new FHC());
        LinkedList<FHeapCell<Elt>> items = new LinkedList<FHeapCell<Elt>>();
        for (int i=0; i<rhs; ++i) {
            items.add(fh.insert(new Elt(heapSize - i)));
        }
        for (int i=0; i<rhs; ++i) {
            FHeapCell<Elt> item = items.get(i);
            item.value().value = -i;
            fh.increasePriority(items.get(i));
        } 
        Elt me = new Elt(Integer.MIN_VALUE);
        fh.maxElt(me);
        while(!fh.isEmpty()) {
            FHeapCell<Elt> ptr = items.removeFirst();
            fh.remove(ptr);
        }
    }

    public void testIterator()
    {
        FibHeap<Elt> fh = new FibHeap<Elt>(new FHC());
        for (int i=0; i<1000; ++i) {
            fh.insert(new Elt(1000 - i));
        }
        int ttl = 0;
        for (Elt e : fh) ttl++;
        assert ttl == 1000;
        fh.extractMin();
        ttl = 0;
        for (Elt e: fh) ttl++;
        assert ttl == 999;
    }


    public static final void main(String[] args)
    {
        FibHeapTest t = new FibHeapTest();
        t.testFibHeap();
    }
}
