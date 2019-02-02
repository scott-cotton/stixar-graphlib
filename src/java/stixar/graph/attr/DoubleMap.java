package stixar.graph.attr;

import java.util.Arrays;

/**
   A map to doubles for {@link Attributable attributable objects}.
 */
public class DoubleMap implements NativeMap
{
    protected double[] data;

    protected DoubleMap(double[] data)
    {
        this.data = data;
    }

    public NativeMap.Type type()
    {
        return NativeMap.Type.Double;
    }

    public double get(int i)
    {
        return data[i];
    }

    public double set(int i, double v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        double[] newData = new double[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        double[] newData = new double[cap];
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
        Arrays.fill(data, 0d);
    }
}
