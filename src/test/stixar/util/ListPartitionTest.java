package stixar.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ListPartitionTest extends TestCase
{
    public ListPartitionTest()
    {
        super("Partition");
    }

    public void testPartition()
    {
        ListPartition<Integer> part = new ListPartition<Integer>();
        int bsz = 5499;
        ArrayList<ListPartition.Block<Integer>> list = new ArrayList<ListPartition.Block<Integer>>(43 * bsz);
        long start = System.currentTimeMillis();
        for (int i=0; i<42; ++i) {
            list.add(part.createBlock(i));
        }
        
        for (int i=0; i<bsz * 42; ++i) {
            ListPartition.Block<Integer> b = list.get(i % 42);
            ListPartition.Block<Integer> nb = part.createBlock(i);
            part.union(nb, b);
            list.add(nb);
        }
        long end = System.currentTimeMillis();
        System.out.println("num partitions: " + part.size());
        System.out.println("num elements: " + (bsz * 42));
        System.out.println("done in " + (end - start) + " millis.");
        for (int i=0; i < list.size(); ++i) {
            ListPartition.Block<Integer> leader = list.get(i % 42);
            ListPartition.Block<Integer> b = list.get(i);
            assertTrue(leader.equals(b));
            assertTrue(b.equals(leader));
        }
        CList<ListPartition.Block<Integer>> copy = new CList<ListPartition.Block<Integer>>();
        copy.addAll(part.blocks());
        int bttl = 42;
        for (ListPartition.Block<Integer> leader : copy) {
            int ttl = 0;
            try {
                for (int i : leader) {
                    ttl++;
                }
                assertTrue(ttl == bsz + 1);
            } catch (ConcurrentModificationException e) {
                assertTrue(false);
            }
            leader.split();
            bttl += bsz;
        }
        assertTrue(bttl == part.size());
    }

    public static void main(String[] args)
    {
        ListPartitionTest t = new ListPartitionTest();
        t.testPartition();
    }
}