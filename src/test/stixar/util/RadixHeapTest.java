package stixar.util;

import java.util.Random;
import junit.framework.TestCase;

public class RadixHeapTest extends TestCase
{

    private static class Elt
    {
        Elt(long l)
        {
            lv = l;
        }

        long lv;
        Cell<Elt> cell;

        public String toString()
        {
            return String.format("Elt[%d]", lv);
        }
    }

    private static class EltToLong 
        implements RadixHeap.LongValuator<Elt>
    {
        public long longValue(Elt elt)
        {
            return elt.lv;
        }
    }

    private Random rnd;

    private static long MaxValue = 1 << 23;
    private static byte step = 8;
    private static int numElts = 1024;

    public RadixHeapTest() 
    {
        super("RadixHeap");
        rnd = new Random();
    }

    public void testMe()
    {
        EltToLong lv = new EltToLong();
        RadixHeap<Elt> rHeap = new RadixHeap<Elt>(lv, RadixHeap.maxBits(MaxValue), step);
        Elt[] elts = new Elt[numElts];
        for (int i=0; i<numElts; ++i) {
            //long l = rnd.nextInt((int) MaxValue);
            long l = i * 100;
            elts[i] = new Elt(l);
            elts[i].cell = rHeap.insert(elts[i]);
        }
        for (int i=0; i<numElts; ++i) {
            if (i % 2 == 0) 
                rHeap.remove(elts[i].cell);
            else if (i % 3 == 0) {
                elts[i].lv = 13000;
                rHeap.requeue(elts[i].cell);
            } else if (i % 5 == 0) {
                elts[i].lv = 2;
                rHeap.requeue(elts[i].cell);
            } else {
                elts[i].lv = 0;
                rHeap.requeue(elts[i].cell);
            }
        }
        while(!rHeap.isEmpty()) {
            Elt elt = rHeap.extractMin();
            System.out.println("extracted " + elt.lv);
        }
    }


    public static void main(String[] args)
    {
        RadixHeapTest t = new RadixHeapTest();
        t.testMe();
    }

}
