package stixar.graph.attr;

import java.util.BitSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;

class ArrayMap<T> implements AttrMap<T>
{
    protected T[] data;
    protected static Logger logger = Logger.getLogger("attr-resize");
    
    protected ArrayMap(T[] data)
    {
        this.data = data;
    }

    public T get(int i)
    {
        return data[i];
    }

    public T set(int i, T v)
    {
        return data[i] = v;
    }

    /**
       Method for use by an attribute manager when this attribute map 
       is managed.
       <p>
       Managed attribute maps may need to grow when the set of attributable
       objects grows.  This method is invoked by an attribute manager
       when the span of integer ids used by a set of attributable objects
       grows, i.e. when the set of attributable objects grows.
       </p>
       @param cap the new capacity of the attribute map.
     */
    @SuppressWarnings("unchecked")
    public void grow(int cap)
    {

        if (cap < data.length)
            throw new IllegalArgumentException
                ("got cap " + cap + " when length was " + data.length);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("growing to capacity " + cap);
        }
        T[] newData = (T[]) java.lang.reflect.Array.newInstance
            (data.getClass().getComponentType(), cap);
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    /**
       Method for use by an attribute manager when this map
       is managed.
       <p>
       </p>
       @param cap the new capacity for the attribute map, which 
       is guaranteed to be less than the current capacity (given
       by the last call to either shrink or grow).
       @param fillPerm a permutation array with the following properties:
       <ol>
       <li>if an attributed object with id <tt>i</tt> does not exist, then
       <tt>fillPerm[i] == -1</tt>.</li>
       <li>if an attributed object with id <tt>i</tt> does exist, then
       <tt>fillPerm[i] &gt;= 0</tt></li>
       <li>Monotonicity: for every pair ids <tt>i,j</tt> with <tt>i&gt;j</tt> and
       for which attributable objects exists,  <tt>fillPerm[i] &gt; fillPerm[j]</tt>
       </li>
       </ol>
     */
    @SuppressWarnings("unchecked")
    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length)
            throw new IllegalArgumentException();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("shrinking to capacity " + cap);
        }
        T[] newData = (T[]) java.lang.reflect.Array.newInstance
            (data.getClass().getComponentType(), cap);
        for (int i=0; i<data.length; ++i) {
            int pi = fillPerm[i];
            if (pi == -1) 
                continue;
            newData[pi] = data[i];
        }
        data = newData;
    }

    public void clear()
    {
        Arrays.fill(data, null);
    }
}
