package stixar.util;

import java.util.Iterator;

/**
   A listable partition structure.
   <p>
   This data structure allows maintaining partitions of sets.
   It is fundamentally like {@link Partition}, but allows 
   a few more convenient operations at the cost of some time
   and space.
   
   As in {@link Partition}, the fundamental operations are 
   <ol>
   <li>{@link ListPartition#createBlock}, the addition of elements to
   the set to be partitioned, which end up in their own singleton
   blocks.</li>
   <li> {@link ListPartition#union unioning} of the blocks of the partitioned set.</li>
   <li> {@link ListPartition#find finding} a representative for each block.</li>
   </ol>
   </p>
   <p>
   Also as in {@link Partition}, the implementation is based on the classical 
   union-find data mechanism  with path compression and weighted union.  Any sequence 
   of <tt>n</tt> <tt>createBlock</tt> and <tt>m&gt;=</tt> other operations 
   ("finds", i.e. tests for equality, or unions) takes <tt>O(m alpha(m,n))</tt> 
   where <tt>alpha(m,n)</tt> is the inverse Ackerman function.  
   For most practical values of <tt>n</tt> and <tt>m</tt>, the runtime is
   linear.
   </p>
   <p>
   Other conveniance methods are provided with a ListPartition.  In particular, one may 
   <ol>
   <li>
   {@link ListPartition.Block#iterator enumerate over the elements} in a block.</li>
   <li>{@link ListPartition.Block#split Split} an entire block into single blocks. </li>
   <li>{@link ListPartition#blocks Enumerate} all the partitions.</li>
   </ol> 
   While these operations do  not change asymptotic run-time of the union/find operations, 
   they induce induce a sizeable constant overhead and are consequently typically 
   several time slower than the {@link Partition basic partition}.
   </p>
  */
public class ListPartition<T>
{
    /**
       A partition block.
       <p>
       This is an opaque class which can only be 
       manipulated via a Partition object.  It may
       be tested for equality against other partition
       blocks, have its elements split into singleton
       blocks, or allow iterating over the contained elements.
       </p>
     */
    public static final class Block<T>  implements Iterable<T>
    {
        ListPartition<T> part;
        Block<T> parent;
        ListCell<Block<T>> cell;
        CList<Block<T>> blocks;
        protected T value;
        int rank;

        /*
          Constructed with createBlock(T v) in ListPartition.
         */
        Block(ListPartition<T> p, T v) 
        {
            part = p;
            parent = this;
            blocks = new CList<Block<T>>();
            blocks.add(this);
            cell = null;
            rank = 0;
            value = v;
        }

        /**
           Make the contained elements iterable.
         */
        public Iterator<T> iterator()
        {
            final Block<T> found = part.find(this);
            return new BlockIterator<T>(found.blocks.iterator());
        }

        /**
           return the total number of elements in this block
           of the partitioning.
         */
        public int size()
        {
            return part.find(this).blocks.size();
        }

        /**
           Test for equality.
         */
        public final boolean equals(Block<T> b)
        {
            return part.find(this) == part.find(b);
        }

        /**
           Split this block into many blocks, one per contained element.
           <p>
           At the end of this operation, this block will contain exactly one
           element.  This operation takes <tt>O(n)</tt> time if there are
           <tt>n</tt> elements in this partition.
           </p>
         */
        public final void split()
        {
            Block<T> found = part.find(this);
            ListPartition<T> part = found.part;
            for (Block<T> b : found.blocks) {
                if (b.cell.isValid()) {
                    part.blocks.remove(b.cell);
                    b.cell = part.blocks.append(b);
                }
                b.parent = b;
                b.rank = 0;
                if (!b.cell.isValid()) {
                    b.cell = part.blocks.append(b);
                }
                b.blocks.clear();
                b.blocks.add(b);
            }
        }
    }

    // store blocks
    protected CList<Block<T>> blocks;
    protected int total;

    /**
       Construct a new partioning of an unknown set.
     */
    public ListPartition()
    {
        blocks = new CList<Block<T>>();
        total = 0;
    }

    /**
       Add a new element v to the set which is partioned,
       giving the element its own partion block.
       
       @param v the element to add to the set which is 
       partitioned.
       @return a block containing the single element <tt>v</tt>
     */
    public Block<T> createBlock(T v)
    {
        Block<T> result = new Block<T>(this, v);
        result.cell = blocks.append(result);
        total++;
        return result;
    }

    /**
       Find a representative block for a given block.
       <p>
       Given two blocks <tt>b1</tt> and <tt>b2</tt>, 
       <tt>b1.equals(b2)</tt> iff <tt>find(b1) == find(b2)</tt>
       </p>
       @return a representative block.
     */
    public Block<T> find(Block<T> b)
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
    public void union(Block<T> a, Block<T> b)
    {
        a = find(a);
        b = find(b);
        if (a.rank > b.rank) {
            b.parent = a;
            a.blocks.append(b.blocks);
            b.blocks = a.blocks;
            if (b.cell.isValid()) {
                blocks.remove(b.cell);
            }
        } else if (a != b) {
            a.parent = b;
            b.blocks.append(a.blocks);
            a.blocks = b.blocks;
            if (a.cell.isValid())
                blocks.remove(a.cell);
            if (a.rank == b.rank) 
                b.rank++;
        }
    }

    /**
       return the number of blocks in the partitioning.
     */
    public int size()
    {
        return blocks.size();
    }

    /**
       Return the total number of elements in the partitioning.
     */
    public int numElements()
    {
        return total;
    }

    /**
       Return a list of representative blocks.
     */
    public CList<Block<T>> blocks()
    {
        return blocks;
    }


    /*
      Simple mapping from Iterator<Block<T>> to Iterator<T>
      by way of the "T value" field in Block<T> objects.
     */
    private static class BlockIterator<T> implements Iterator<T>
    {
        private Iterator<Block<T>> bIter;

        BlockIterator(Iterator<Block<T>> bIter) 
        {
            this.bIter = bIter;
        }

        public boolean hasNext()
        {
            return bIter.hasNext();
        }

        public T next()
        {
            return bIter.next().value;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}