package stixar.graph.attr;

import java.util.Arrays;

/**
   A map to chars for {@link Attributable attributable objects}.
 */
public class CharMap implements NativeMap
{
    protected char[] data;

    protected CharMap(char[] data)
    {
        this.data = data;
    }

    public NativeMap.Type type()
    {
        return NativeMap.Type.Char;
    }

    public char get(int i)
    {
        return data[i];
    }

    public char set(int i, char v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        char[] newData = new char[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        char[] newData = new char[cap];
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
        Arrays.fill(data, (char) 0);
    }

}
