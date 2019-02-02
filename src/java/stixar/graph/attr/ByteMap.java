package stixar.graph.attr;

import java.util.Arrays;
/**
   A map to bytes for {@link Attributable attributable objects}.
 */
public class ByteMap implements NativeMap
{
    protected byte[] data;

    protected ByteMap(byte[] data)
    {
        this.data = data;
    }

    public NativeMap.Type type()
    {
        return NativeMap.Type.Byte;
    }

    public byte get(int i)
    {
        return data[i];
    }

    public byte set(int i, byte v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        byte[] newData = new byte[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        byte[] newData = new byte[cap];
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
        Arrays.fill(data, (byte) 0);
    }

}
