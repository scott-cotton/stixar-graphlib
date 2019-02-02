package stixar.graph.attr;

import stixar.graph.Node;
import stixar.graph.Edge;

import java.util.Map;
import java.util.HashMap;
import java.util.BitSet;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
   Implementation of graph attributes.
   <p>

   This class provides an implementation of the {@link GraphAttrCollection} 
   interface.  For details about managed and unmanaged attributes, 
   please refer to the interface documentation.
 */
public class AttrManager implements GraphAttrCollection
{
    public static int DefaultMaxNodeAlloc = 1 << 16;
    public static int DefaultMaxEdgeAlloc = 1 << 16;
    
    protected int nodeTop;
    protected int nodeCap;
    protected int edgeTop;
    protected int edgeCap;

    protected BitSet nSlots;
    protected BitSet eSlots;

    protected HashMap<Object, NodeData> nodeAttrs;
    protected HashMap<Object, EdgeData> edgeAttrs;
    protected HashMap<Object, Node> specialNodes;
    protected HashMap<Object, Edge> specialEdges;

    protected static Logger logger = Logger.getLogger("attr");

    protected AttrManager(int nCap, int eCap)
    {
        nodeTop = 0;
        nodeCap = nCap;
        edgeTop = 0;
        edgeCap = eCap;
        nSlots = new BitSet(nCap);
        eSlots = new BitSet(eCap);

        nodeAttrs = new HashMap<Object, NodeData>(5);
        edgeAttrs = new HashMap<Object, EdgeData>(3);
        specialNodes = new HashMap<Object, Node>(5);
        specialEdges = new HashMap<Object, Edge>(1);
    }

    /*
      Getting and setting things in the signature.
     */
    @SuppressWarnings("unchecked")
    public <T> NodeMap<T> createNodeMap(Object key)
    {
        NodeMap<T> res = createNodeMap();
        nodeAttrs.put(key, res);
        return res;
    }

    public <T> NodeMatrix<T> createNodeMatrix(Object key)
    {
        NodeMatrix<T> res = createNodeMatrix();
        nodeAttrs.put(key, res);
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> NodeMap<T> getNodeMap(Object key)
    {
        return (NodeMap<T>) nodeAttrs.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> NodeMatrix<T> getNodeMatrix(Object key)
    {
        return (NodeMatrix<T>) nodeAttrs.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> EdgeMap<T> createEdgeMap(Object key)
    {
        EdgeMap<T> res = createEdgeMap();
        edgeAttrs.put(key, res);
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> EdgeMap<T> getEdgeMap(Object key)
    {
        return (EdgeMap<T>) edgeAttrs.get(key);
    }

    public NativeNodeMap getNativeNodeMap(Object key)
    {
        return (NativeNodeMap) nodeAttrs.get(key);
    }

    public NativeEdgeMap getNativeEdgeMap(Object key)
    {
        return (NativeEdgeMap) edgeAttrs.get(key);
    }

    public NativeNodeMatrix getNativeNodeMatrix(Object key)
    {
        return (NativeNodeMatrix) nodeAttrs.get(key);
    }

    public IntNodeMap createIntNodeMap(Object key)
    {
        IntNodeMap result = createIntNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public IntNodeMatrix createIntNodeMatrix(Object key)
    {
        IntNodeMatrix result = createIntNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }

    public FloatNodeMap createFloatNodeMap(Object key)
    {
        FloatNodeMap result = createFloatNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public FloatNodeMatrix createFloatNodeMatrix(Object key)
    {
        FloatNodeMatrix result = createFloatNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }


    public LongNodeMap createLongNodeMap(Object key)
    {
        LongNodeMap result = createLongNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public LongNodeMatrix createLongNodeMatrix(Object key)
    {
        LongNodeMatrix result = createLongNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }

    public DoubleNodeMap createDoubleNodeMap(Object key)
    {
        DoubleNodeMap result = createDoubleNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public DoubleNodeMatrix createDoubleNodeMatrix(Object key)
    {
        DoubleNodeMatrix result = createDoubleNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }


    public CharNodeMap createCharNodeMap(Object key)
    {
        CharNodeMap result = createCharNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public CharNodeMatrix createCharNodeMatrix(Object key)
    {
        CharNodeMatrix result = createCharNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }

    public ByteNodeMap createByteNodeMap(Object key)
    {
        ByteNodeMap result = createByteNodeMap();
        nodeAttrs.put(key, result);
        return result;
    }

    public ByteNodeMatrix createByteNodeMatrix(Object key)
    {
        ByteNodeMatrix result = createByteNodeMatrix();
        nodeAttrs.put(key, result);
        return result;
    }

    public IntEdgeMap createIntEdgeMap(Object key)
    {
        IntEdgeMap result = createIntEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }

    public FloatEdgeMap createFloatEdgeMap(Object key)
    {
        FloatEdgeMap result = createFloatEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }

    public LongEdgeMap createLongEdgeMap(Object key)
    {
        LongEdgeMap result = createLongEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }

    public DoubleEdgeMap createDoubleEdgeMap(Object key)
    {
        DoubleEdgeMap result = createDoubleEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }


    public CharEdgeMap createCharEdgeMap(Object key)
    {
        CharEdgeMap result = createCharEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }


    public ByteEdgeMap createByteEdgeMap(Object key)
    {
        ByteEdgeMap result = createByteEdgeMap();
        edgeAttrs.put(key, result);
        return result;
    }

    public void removeNodeMap(Object key)
    {
        nodeAttrs.remove(key);
    }

    public void removeEdgeMap(Object key)
    {
        edgeAttrs.remove(key);
    }

    public void registerNode(Object key, Node n)
    {
        specialNodes.put(key, n);
    }

    public void unregisterNode(Object key)
    {
        specialNodes.remove(key);
    }

    public void registerEdge(Object key, Edge e)
    {
        specialEdges.put(key, e);
    }

    public void unregisterEdge(Object key)
    {
        specialEdges.remove(key);
    }

    /*
      creating unmanaged things in the signature.
     */
    @SuppressWarnings("unchecked")
    public <T> NodeMap<T> createNodeMap()
    {
        T[] store = (T[]) new Object[nodeCap];
        NodeMap<T> res = new ArrayNodeMap<T>(store);
        return res;
    }

    @SuppressWarnings("unchecked")
    public <T> NodeMatrix<T> createNodeMatrix()
    {
        T[][] store = (T[][]) new Object[nodeCap][nodeCap];
        return new ArrayNodeMatrix<T>(store);
    }

    @SuppressWarnings("unchecked")
    public <T> EdgeMap<T> createEdgeMap()
    {
        T[] store = (T[]) new Object[edgeCap];
        EdgeMap<T> res = new ArrayEdgeMap<T>(store);
        return res;
    }

    public IntNodeMap createIntNodeMap()
    {
        int[] istore = new int[nodeCap];
        IntNodeMap result = new IntNodeMap(istore);
        return result;
    }

    public IntNodeMatrix createIntNodeMatrix()
    {
        int[][] istore = new int[nodeCap][nodeCap];
        IntNodeMatrix result = new IntNodeMatrix(istore);
        return result;
    }

    public FloatNodeMap createFloatNodeMap()
    {
        float[] istore = new float[nodeCap];
        FloatNodeMap result = new FloatNodeMap(istore);
        return result;
    }

    public FloatNodeMatrix createFloatNodeMatrix()
    {
        float[][] istore = new float[nodeCap][nodeCap];
        FloatNodeMatrix result = new FloatNodeMatrix(istore);
        return result;
    }

    public LongNodeMap createLongNodeMap()
    {
        long[] istore = new long[nodeCap];
        LongNodeMap result = new LongNodeMap(istore);
        return result;
    }

    public LongNodeMatrix createLongNodeMatrix()
    {
        long[][] istore = new long[nodeCap][nodeCap];
        LongNodeMatrix result = new LongNodeMatrix(istore);
        return result;
    }

    public DoubleNodeMap createDoubleNodeMap()
    {
        double[] istore = new double[nodeCap];
        DoubleNodeMap result = new DoubleNodeMap(istore);
        return result;
    }

    public DoubleNodeMatrix createDoubleNodeMatrix()
    {
        double[][] istore = new double[nodeCap][nodeCap];
        DoubleNodeMatrix result = new DoubleNodeMatrix(istore);
        return result;
    }

    public CharNodeMap createCharNodeMap()
    {
        char[] istore = new char[nodeCap];
        CharNodeMap result = new CharNodeMap(istore);
        return result;
    }

    public CharNodeMatrix createCharNodeMatrix()
    {
        char[][] istore = new char[nodeCap][nodeCap];
        CharNodeMatrix result = new CharNodeMatrix(istore);
        return result;
    }

    public ByteNodeMap createByteNodeMap()
    {
        byte[] istore = new byte[nodeCap];
        ByteNodeMap result = new ByteNodeMap(istore);
        return result;
    }

    public ByteNodeMatrix createByteNodeMatrix()
    {
        byte[][] istore = new byte[nodeCap][nodeCap];
        ByteNodeMatrix result = new ByteNodeMatrix(istore);
        return result;
    }

    public IntEdgeMap createIntEdgeMap()
    {
        int[] istore = new int[edgeCap];
        IntEdgeMap result = new IntEdgeMap(istore);
        return result;
    }

    public FloatEdgeMap createFloatEdgeMap()
    {
        float[] istore = new float[edgeCap];
        FloatEdgeMap result = new FloatEdgeMap(istore);
        return result;
    }

    public LongEdgeMap createLongEdgeMap()
    {
        long[] istore = new long[edgeCap];
        LongEdgeMap result = new LongEdgeMap(istore);
        return result;
    }

    public DoubleEdgeMap createDoubleEdgeMap()
    {
        double[] istore = new double[edgeCap];
        DoubleEdgeMap result = new DoubleEdgeMap(istore);
        return result;
    }

    public CharEdgeMap createCharEdgeMap()
    {
        char[] istore = new char[edgeCap];
        CharEdgeMap result = new CharEdgeMap(istore);
        return result;
    }


    public ByteEdgeMap createByteEdgeMap()
    {
        byte[] istore = new byte[edgeCap];
        ByteEdgeMap result = new ByteEdgeMap(istore);
        return result;
    }

    public void clear()
    {
        for (NodeData nd : nodeAttrs.values())
            nd.clear();
        for (EdgeData ed : edgeAttrs.values())
            ed.clear();
    }



    /*
      Allocation management.
     */
    public void ensureCapacity(int n, int m)
    {
        if (nodeCap < n) {
            for (NodeData nd : nodeAttrs.values()) {
                nd.grow(n);
            }
            nodeCap = n;
        }
        if (edgeCap < m) {
            for (EdgeData ed : edgeAttrs.values()) {
                ed.grow(m);
            }
            edgeCap = m;
        }
    }

    protected void growNodes(int newCap)
    {
        assert newCap > nodeCap;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("growing nodes to " + newCap);
        }
        for (NodeData nd : nodeAttrs.values()) {
            nd.grow(newCap);
        }
        nodeCap = newCap;
    }

    protected void growEdges(int newCap)
    {
        assert newCap > edgeCap;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("growing edges to " + newCap);
        }
        for (EdgeData ed : edgeAttrs.values()) {
            ed.grow(newCap);
        }
        edgeCap = newCap;
    }


    protected int[] shrinkNodes(int newCap)
    {
        assert newCap < nodeCap;
        if (logger.isLoggable(Level.FINE)) {
            logger.info("shrinking nodes to " + newCap);
        }
        int[] perm = new int[nodeCap];
        int newTop = 0;
        for (int i=0; i<nodeCap; ++i) {
            if (i < nodeTop && nSlots.get(i)) {
                perm[i] = newTop++;
            } else {
                perm[i] = -1;
            }
        }
        for (NodeData nd : nodeAttrs.values()) {
            nd.shrink(newCap, perm);
        }
        nodeCap = newCap;
        nodeTop = newTop;
        return perm;
    }

    protected int[] shrinkEdges(int newCap)
    {
        assert newCap < edgeCap;
        if (logger.isLoggable(Level.FINE)) {
            logger.info("shrinking nodes to " + newCap);
        }
        int[] perm = new int[edgeCap];
        int newTop = 0;
        for (int i=0; i<edgeCap; ++i) {
            if (i < edgeTop && eSlots.get(i)) {
                perm[i] = newTop++;
            } else {
                perm[i] = -1;
            }
        }
        for (EdgeData ed : edgeAttrs.values()) {
            ed.shrink(newCap, perm);
        }
        edgeCap = newCap;
        edgeTop = newTop;
        return perm;
    }

    /*
      Map Callbacks
     */

    /*
      For callbacks on attributes once
      that becomes a feature.
     */
    public void newNode(Node n)
    {
    }

    /*
      For callbacks on attributes once
      that becomes a feature.
     */
    public void remove(Node n)
    {
    }

    /*
      For callbacks on attributes once
      that becomes a feature.
     */
    public void remove(Edge e)
    {
    }

    /*
      For callbacks on attributes once
      that becomes a feature.
     */
    protected void newEdge(Edge e)
    {
    }

}
