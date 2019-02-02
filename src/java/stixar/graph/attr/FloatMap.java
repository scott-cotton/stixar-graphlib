package stixar.graph.attr;

import java.util.Arrays;
/**
   A map to doubles for {@link Attributable attributable objects}.
 */
public class FloatMap implements NativeMap
{
    protected float[] data;
    
    public FloatMap(float[] data)
    {
        this.data = data;
    }
    
    public NativeMap.Type type()
    {
        return NativeMap.Type.Float;
    }

    public float get(int i)
    {
        return data[i];
    }

    public float set(int i, float v)
    {
        return data[i] = v;
    }

    public void grow(int cap)
    {
        if (cap < data.length)
            throw new IllegalArgumentException();

        float[] newData = new float[cap];
        System.arraycopy(data, 0, newData, 0, cap);
        data = newData;
    }

    public void shrink(int cap, int[] fillPerm)
    {
        if (cap > data.length || fillPerm.length != data.length)
            throw new IllegalArgumentException();

        float[] newData = new float[cap];
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
        Arrays.fill(data, 0f);
    }

}
