package stixar.util;

/**
   A partition structure.
   <p>
   This data structure allows maintaining partitions of sets.
   The fundamental operations are 
   <ol>
   <li>{@link Partition#createBlock}, the addition of elements to
   the set to be partitioned, which end up in their own singleton
   blocks</li>
   <li> {@link Partition#union unioning} of the blocks of the partitioned set</li>
   <li> <tt>{@link Partition#find finding}</tt> a representative for each block</li>
   </ol>
   </p>
   <p>
   The implementation is the classical union-find data mechanism
   with path compression and weighted union.  Any sequence 
   of <tt>n</tt> <tt>createBlock</tt> and <tt>m &gt;=n</tt> other operations 
   ("finds", i.e. tests for equality, or unions) takes <tt>O(m alpha(m,n))</tt> 
   where <tt>alpha(m,n)</tt> is the inverse Ackerman function.  
   For most practical values of <tt>n</tt> and <tt>m</tt>, the runtime is
   linear.
   </p>
   @see ListPartition
   
 */
public class Partition
{
    /**
       A partition block.
       <p>
       This is an opaque class which can only be 
       manipulated via a Partition object.  It may
       only be tested for equality against other partition
       blocks.
       </p>
     */
    public static final class Block  
    {
        Partition part;
        Block parent;
        int rank;
        int size;

        Block(Partition p)
        {
            part = p;
            parent = this;
            rank = 0;
            size = 1;
        }

        /**
           Return the total number of elements in this Block.
         */
        public int size()
        {
            return part.find(this).size;
        }

        /**
           Test for equality.
         */
        public final boolean equals(Block b)
        {
            return part.find(this) == part.find(b);
        }
    }

    protected int totalElements;
    protected int totalBlocks;

    /**
       Construct a new partioning of an unknown set.
     */
    public Partition()
    {
        totalElements = 0;
        totalBlocks = 0;
    }

    /**
       Add a new element v to the set which is partioned,
       giving the element its own partion block.
       
       @return a block containing the single element <tt>v</tt>
     */
    public Block createBlock()
    {
        Block result = new Block(this);
        totalElements++;
        totalBlocks++;
        return result;
    }

    /**
       Find a representative Block for a given Block.
       <p>
       Given two blocks <tt>b1</tt> and <tt>b2</tt>, 
       <tt>b1.equals(b2)</tt> iff <tt>find(b1) == find(b2)</tt>
       </p>
       @return a representative block.
     */
    public final Block find(Block b)
    {
        if (b.parent != b) {
            b.parent = find(b.parent);
        }
        return b.parent;
    }

    /**
       Construct the union of two blocks.

       @param a the first block to be unioned.
       @param b the second block to be unioned
     */
    public void union(Block a, Block b)
    {
        a = find(a);
        b = find(b);
        if (a.rank > b.rank) {
            b.parent = a;
            a.size += b.size;
            totalBlocks--;
        } else if (a != b) {
            a.parent = b;
            b.size += a.size;
            if (a.rank == b.rank) 
                b.rank++;
            totalBlocks--;
        }
    }

    /**
       Return the number of blocks in the partitioning.
     */
    public int size()
    {
        return totalBlocks;
    }

    /**
       Return the number of elements in the partitioning.

       @return the sum of the sizes of all the blocks.
     */
    public int totalElements()
    {
        return totalElements;
    }

}

