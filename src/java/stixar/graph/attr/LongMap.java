package stixar.graph.attr;

import java.util.Arrays;
/**
   A map to longs for {@link Attributable attributable objects}.
 */
public class LongMap implements NativeMap
{

    protected long[] data;

    protected LongMap(long[] data)
    {
        this.data = data;
    }

    public NativeMap.Type type()
    {
        return NativeMap.Type.Long;
    }

    public long get(int i)
    {
        return data[i];
    }

    public long set(int i, long v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        long[] newData = new long[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        long[] newData = new long[cap];
        for (int i=0; i<data.length; ++i) {
            int pi = fillPerm[i];
            if (pi != -1) {
                newData[pi] = data[i];
            }
        }
        data = newData;
    }

    public void clear()
    {
        Arrays.fill(data, 0L);
    }
        
}
