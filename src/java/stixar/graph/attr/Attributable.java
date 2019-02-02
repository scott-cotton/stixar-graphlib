package stixar.graph.attr;

import java.util.BitSet;

/**
   Attributable objects. 
   <p>
   Attributable objects subscribe to a contract
   in which they have <tt>int</tt> identifiers which are unique
   and either use their attributability in an <em>ad hoc</em>
   manner, or use it via managed attributes.
   </p><p>
   Ad hoc use allows getting and setting values in arbitrary arrays
   (or BitSets) but requires that the attribute group to which the attributable 
   object belongs does not change during the lifetime of the 
   array.  The attributable group is typically a set of nodes or edges 
   in a graph.  Ad hoc attribution requires one easily inlinable
   method call per get/set.
   </p>
   <p>
   managed attributes are created via an {@link AttrManager}, which
   in turn keeps the attributable objects aligned with their attributes
   in the face of changes in the respective attribute groups.  Managed
   attributes require two easily inlinable method calls per get or set.
   </p>
   Managed attributes allow symmetric syntax according to preference. 
   Given an attributable object <tt>a</tt> and a managed attribute map
   <tt>m</tt> such as a {@link NodeMap}, one can either
   <ol>
   <li><tt>a.get(m)</tt>, <tt>a.set(m, v)</tt></li>
   <li><tt>m.get(a)</tt>, <tt>m.set(a, v)</tt></li>
   </ol>
   However, in the case that the managed attribute map is native, such as 
   an {@link IntEdgeMap}, the respective <tt>get</tt> and <tt>set</tt>
   method names for the attributable items append the native type
   (e.g. <tt>getInt</tt>, and <tt>setInt</tt>).  The native maps do
   undergo this transformation but rather keep the <tt>get</tt>
   and <tt>set</tt> methods, which seems more intuitive for maps.

   @see NodeMap EdgeMap AttrManager NativeMap
 */
public interface Attributable
{
    /**
       Array attribute getter.
       @param attrs an attribute array containing attributes
       for a node or edge.
       @return the attribute for the node or edge.
     */
    public <T> T get(T[] attrs);

    /**
       Source attribute getter
       @param source an AttrSource which supplies the attribute data
       @return the attribute for the node or edge.
     */
    public <T> T get(AttrSource<T> source);

    /**
       Array attribute setter.

       @param attrs an attribute array containing attributes
       for a node or edge.
       @param v the new value for the attribute.
       @return v
     */
    public <T> T set(T[] attrs, T v);

    /**
       Sink attribute setter
       @param sink an AttrSink which sets the attribute data.
       @return v
     */
    public <T> T set(AttrSink<T> sink, T v);

    /**
       Array attribute getter.
     */
    public int getInt(int[] attrs);

    /**
       Array attribute setter for native ints.
     */
    public int setInt(int[] attrs, int v);

    /**
       Array attribute getter for native floats.
     */
    public float getFloat(float[] attrs);

    /**
       Array attribute setter for native floats.
     */
    public float setFloat(float[] attrs, float v);

    /**
       Array attribute getter for native longs.
     */
    public long getLong(long[] attrs);

    /**
       Array attribute setter for native longs.
     */
    public long setLong(long[] attrs, long v);

    /**
       Array attribute getter for native doubles.
     */
    public double getDouble(double[] attrs);

    /**
       Array attribute setter for native doubles.
     */
    public double setDouble(double[] attrs, double v);

    /**
       Array attribute getter for native booleans.
     */
    public boolean getBool(boolean[] attrs);

    /**
       Array attribute setter for native booleans.
     */
    public boolean setBool(boolean[] attrs, boolean b);

    /**
       Array attribute getter for native chars.
     */
    public char getChar(char[] attrs);

    /**
       Array attribute setter for native chars.
     */
    public char setChar(char[] attrs, char c);

    /**
       Array attribute getter for native bytes.
     */
    public byte getByte(byte[] attrs);

    /**
       Array attribute setter for native bytes.
     */
    public byte setByte(byte[] attrs, byte c);

    public boolean in(BitSet set);
    public boolean in(BitSet set, boolean v);

    public int getInt(IntMap m);
    public int setInt(IntMap m, int v);

    public long getLong(LongMap m);
    public long setLong(LongMap m, long v);

    public float getFloat(FloatMap m);
    public float setFloat(FloatMap m, float v);

    public double getDouble(DoubleMap m);
    public double setDouble(DoubleMap m, double v);

    public char getChar(CharMap m);
    public char setChar(CharMap m, char v);

    public byte getByte(ByteMap m);
    public byte setByte(ByteMap m, byte v);





}
