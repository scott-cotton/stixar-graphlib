package stixar.graph.attr;

import java.util.BitSet;

/**
   Implementation of array attributes for arbitrary objects.
 */
public abstract class AttributableBase implements Attributable
{
    private int index;

    /*
      use this index into the arra for everything.
     */
    protected AttributableBase(int index)
    {
        this.index = index;
    }

    /**
       Array attribute getter.
     */
    public final <T> T get(T[] attrs)
    {
        return attrs[index];
    }

    /**
       Map attribute getter.
     */
    public final <T> T get(AttrSource<T> map)
    {
        return map.get(index);
    }

    /**
       Array attribute setter.
     */
    public final <T> T set(T[] attrs, T v)
    {
        return attrs[index] = v;
    }


    /**
       Sink attribute setter.
     */
    public final <T> T set(AttrSink<T> map, T v)
    {
        return map.set(index, v);
    }

    /**
       Array attribute getter.
     */
    public final int getInt(int[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native ints.
     */
    public final int setInt(int[] attrs, int v)
    {
        return attrs[index] = v;
    }

    /**
       Array attribute getter for native floats.
     */
    public final float getFloat(float[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native floats.
     */
    public final float setFloat(float[] attrs, float v)
    {
        return attrs[index] = v;
    }

    /**
       Array attribute getter for native longs.
     */
    public final long getLong(long[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native longs.
     */
    public final long setLong(long[] attrs, long v)
    {
        return attrs[index] = v;
    }

    /**
       Array attribute getter for native doubles.
     */
    public final double getDouble(double[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native doubles.
     */
    public final double setDouble(double[] attrs, double v)
    {
        return attrs[index] = v;
    }

    /**
       Array attribute getter for native booleans.
     */
    public final boolean getBool(boolean[] attrs)
    {
        return attrs[index];
    }
    /**
       Array attribute setter for native booleans.
     */
    public final boolean setBool(boolean[] attrs, boolean b)
    {
        return attrs[index] = b;
    }

    /**
       Array attribute getter for native chars.
     */
    public final char getChar(char[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native chars.
     */
    public final char setChar(char[] attrs, char c)
    {
        return attrs[index] = c;
    }

    /**
       Array attribute getter for native chars.
     */
    public final byte getByte(byte[] attrs)
    {
        return attrs[index];
    }

    /**
       Array attribute setter for native bytes.
     */
    public final byte setByte(byte[] attrs, byte c)
    {
        return attrs[index] = c;
    }

    public final boolean in(BitSet set)
    {
        return set.get(index);
    }

    public final boolean in(BitSet set, boolean b)
    {
        boolean result = set.get(index);
        set.set(index, b);
        return result;
    }

    protected void setAttrIndex(int i)
    {
        index = i;
    }

    public final int getInt(IntMap m)
    {
        return m.get(index);
    }

    public final int setInt(IntMap m, int v)
    {
        return m.set(index, v);
    }

    public final double getDouble(DoubleMap m)
    {
        return m.get(index);
    }

    public final double setDouble(DoubleMap m, double v)
    {
        return m.set(index, v);
    }


    public final float getFloat(FloatMap m)
    {
        return m.get(index);
    }

    public final float setFloat(FloatMap m, float v)
    {
        return m.set(index, v);
    }

    public final long getLong(LongMap m)
    {
        return m.get(index);
    }

    public final long setLong(LongMap m, long v)
    {
        return m.set(index, v);
    }


    public final char getChar(CharMap m)
    {
        return m.get(index);
    }

    public final char setChar(CharMap m, char v)
    {
        return m.set(index, v);
    }


    public final byte getByte(ByteMap m)
    {
        return m.get(index);
    }

    public final byte setByte(ByteMap m, byte v)
    {
        return m.set(index, v);
    }

}
