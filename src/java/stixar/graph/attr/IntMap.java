package stixar.graph.attr;

import java.util.Arrays;
/**
   A map to ints for {@link Attributable attributable objects}.
 */
public class IntMap implements NativeMap
{
    protected int[] data;
    
    public IntMap(int[] data)
    {
        this.data = data;
    }
    
    public NativeMap.Type type()
    {
        return NativeMap.Type.Int;
    }

    public int get(int i)
    {
        return data[i];
    }

    public int set(int i, int v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        int[] newData = new int[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        int[] newData = new int[cap];
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
        Arrays.fill(data, 0);
    }

}
