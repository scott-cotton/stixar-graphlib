package stixar.graph.attr;

import stixar.graph.Node;

import java.util.Arrays;

/**
   A map to doubles for {@link Attributable attributable objects}.
 */
public class DoubleNodeMatrix implements NativeNodeMatrix
{
    protected double[][] data;
    
    public DoubleNodeMatrix(double[][] data)
    {
        this.data = data;
    }
    
    public NativeMap.Type type()
    {
        return NativeMap.Type.Double;
    }

    public double get(Node u, Node v)
    {
        return data[u.nodeId()][v.nodeId()];
    }

    public double set(Node u, Node v, double val)
    {
        return data[u.nodeId()][v.nodeId()] = val;
    }

    /**
       Method for use by an attribute manager when this attribute map 
       is managed.
       <p>
       Managed attribute maps may need to grow when the set of attributable
       objects grows.  This method is invoked by an attribute manager
       when the span of doubleeger ids used by a set of attributable objects
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
        double[][] newData = new double[cap][cap];
        int i=0;
        for(double[] a : data) {
            double[] newA = new double[cap];
            System.arraycopy(a, 0, newA, 0, a.length);
            newData[i++] = newA;
        }
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
        double [][] newData = new double[cap][cap];
        for (int i=0; i<data.length; ++i) {
            int pi = fillPerm[i];
            if (pi == -1) 
                continue;
            double [] newA = new double[cap];
            for (int j=0; j<cap; ++j) {
                int pj = fillPerm[j];
                if (pj == -1) 
                    continue;
                newA[pj] = data[i][j];
            }
            newData[pi] = newA;
        }
        data = newData;
    }

    public void clear()
    {
        for (int i=0; i<data.length; ++i)
            Arrays.fill(data[i], 0d);
    }
}
