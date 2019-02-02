package stixar.util;

import junit.framework.TestCase;

import java.util.ArrayList;

public class PartitionTest extends TestCase
{
    public PartitionTest()
    {
        super("Partition");
    }

    public void testPartition()
    {
        Partition part = new Partition();
        int bsz = 5499;
        ArrayList<Partition.Block> list = new ArrayList<Partition.Block>(43 * bsz);
        long start = System.currentTimeMillis();
        for (int i=0; i<42; ++i) {
            list.add(part.createBlock());
        }
        
        for (int i=0; i<bsz * 42; ++i) {
            Partition.Block b = list.get(i % 42);
            Partition.Block nb = part.createBlock();
            part.union(nb, b);
            list.add(nb);
        }
        long end = System.currentTimeMillis();
        System.out.println("num partitions: " + part.size());
        System.out.println("num elements: " + (bsz * 42));
        System.out.println("done in " + (end - start) + " millis.");
        for (int i=0; i < list.size(); ++i) {
            Partition.Block leader = list.get(i % 42);
            Partition.Block b = list.get(i);
            assertTrue(leader.equals(b));
            assertTrue(b.equals(leader));
        }
    }

    public static void main(String[] args)
    {
        PartitionTest t = new PartitionTest();
        t.testPartition();
    }
}