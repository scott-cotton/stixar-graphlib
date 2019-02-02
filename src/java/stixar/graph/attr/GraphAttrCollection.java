package stixar.graph.attr;

import stixar.graph.Node;
import stixar.graph.Edge;

/**
   Interface for managed graph attributes.
   <p>
   This interface defines methods for managing attributes of 
   graphs.  The node and edge attributes fall into two categories
   <ol>
   <li>Managed Attribute Maps<br></br>
   Managed maps are guaranteed to retain their integrity when 
   a graph undergoes topologic changes by way of methods in 
   {@link stixar.graph.MutableGraph}.  All managed maps 
   are associated with keys.  When a managed map is no longer
   needed, it must be explicitly removed from the attribute
   collection in order to become eligible for garbage collection.
   </li>
   <li>Ad Hoc Attribute Maps<br></br>
   Ad Hoc Maps are not guaranteed to retain their integrity 
   when a graph undergoes topologic changes.  Their behavior
   in the face of topologic changes are undefined.  They may
   return an appropriate value, an inappropriate value, or 
   throw an exception if a topologic change occurs after their
   creation.  They are not associated with keys in the attribute 
   collection.  They need not be removed to become eligible for garbage 
   collection. 
   </li>
   </ol>
   </p>
   <p>
   In addition, several methods allow distinguishing individual
   nodes and edges.
   </p>

   @see Attributable
 */
public interface GraphAttrCollection
{
    /**
       Create a generic node attribute map with key <tt>key</tt>.
       The default value is <tt>null</tt>.

       @param key the key of the node map.
       @return an <em>managed</em> attribute map for the nodes in 
       this graph.
     */
    public <T> NodeMap<T> createNodeMap(Object key);

    /**
       Create a generic node attribute matrix with key <tt>key</tt>.
       The default value is <tt>null</tt>.

       @param key the key of the node map.
       @return an <em>managed</em> attribute matrix for the nodes in 
       this graph.
     */
    public <T> NodeMatrix<T> createNodeMatrix(Object key);

    /**
       Find a node attribute map by key.

       @param key
       argument must have the same type as the type paramater <tt>T</tt>.
       @return the node attribute map keyed with <tt>key</tt>, or 
       <tt>null</tt> if no such map exists.
     */
    public <T> NodeMap<T> getNodeMap(Object key);

    /**
       Find a node attribute map by key.

       @param key
       argument must have the same type as the type paramater <tt>T</tt>.
       @return the node attribute map keyed with <tt>key</tt>, or 
       <tt>null</tt> if no such map exists.
     */
    public <T> NodeMatrix<T> getNodeMatrix(Object key);

    /**
       Create a generic edge attribute map with key <tt>key</tt>.
       The default value is <tt>null</tt>.

       @param key the key of the node map.
     */
    public <T> EdgeMap<T> createEdgeMap(Object key);

    /**
       Find an edge attribute map by key.

       @param key
       argument must have the same type as the type paramater <tt>T</tt>.
       @return the node attribute map keyed with <tt>key</tt>, or 
       <tt>null</tt> if no such map exists.
       @throws ClassCastException if there is a node map associated
       with <tt>key</tt> which is not of type EdgeMap&lt;T&gt;.
     */
    public <T> EdgeMap<T> getEdgeMap(Object key);

    /**
       Create a new native int node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native int node attribute map.
     */
    public IntNodeMap createIntNodeMap(Object key);

    /**
       Retrieve a managed native int node attribute map associated with a key.
       @param key the key of the new map.
       @return the managed native int node attribute map associated with <tt>key</tt>,
       or <tt>null</tt> if no such map exists.
       @throws ClassCastException if there is a node map associated
       with <tt>key</tt> which is not of type NativeNodeMap.
     */
    public NativeNodeMap getNativeNodeMap(Object key);

    /**
       Create a new native int node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native int node attribute matrix.
     */
    public IntNodeMatrix createIntNodeMatrix(Object key);

    /**
       Retrieve a native int node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native int node attribute matrix, or 
       <tt>null</tt> if no such matrix exists.
       @throws ClassCastException if there is a node map or matrix associated
       with <tt>key</tt> which is not of type NativeNodeMatrix.
     */
    public NativeNodeMatrix getNativeNodeMatrix(Object key);

    /**
       Create a new native float node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native float node attribute map.
     */
    public FloatNodeMap createFloatNodeMap(Object key);

    /**
       Create a new native float node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native float node attribute matrix.
     */
    public FloatNodeMatrix createFloatNodeMatrix(Object key);

    /**
       Create a new native long node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native long node attribute map.
     */
    public LongNodeMap createLongNodeMap(Object key);

    /**
       Create a new native long node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native long node attribute matrix.
     */
    public LongNodeMatrix createLongNodeMatrix(Object key);
    /**
       Create a new native double node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native double node attribute map.
     */
    public DoubleNodeMap createDoubleNodeMap(Object key);

    /**
       Create a new native double node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native double node attribute matrix.
     */
    public DoubleNodeMatrix createDoubleNodeMatrix(Object key);
    /**
       Create a new native char node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native char node attribute map.
     */
    public CharNodeMap createCharNodeMap(Object key);
    /**
       Create a new native char node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native char node attribute matrix.
     */
    public CharNodeMatrix createCharNodeMatrix(Object key);

    /**
       Create a new native byte node attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native byte node attribute map.
     */
    public ByteNodeMap createByteNodeMap(Object key);

    /**
       Create a new native byte node attribute matrix associated with a key.
       @param key the key of the new matrix.
       @return a managed native byte node attribute matrix.
     */
    public ByteNodeMatrix createByteNodeMatrix(Object key);

    /**
       Retrieve a native edge map by key.
       @param key the key of the map to be retrieved.
       @return the NativeEdgeMap associated with <tt>key</tt>
       @throws ClassCastException if the edge data associated 
       with <tt>key</tt> is not of type NativeEdgeMap.
     */
    public NativeEdgeMap getNativeEdgeMap(Object key);

    /**
       Create a new native int edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native int edge attribute map.
     */
    public IntEdgeMap createIntEdgeMap(Object key);

    /**
       Create a new native float edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native float edge attribute map.
     */
    public FloatEdgeMap createFloatEdgeMap(Object key);

    /**
       Create a new native long edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native long edge attribute map.
     */
    public LongEdgeMap createLongEdgeMap(Object key);

    /**
       Create a new native double edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native double edge attribute map.
     */
    public DoubleEdgeMap createDoubleEdgeMap(Object key);

    /**
       Create a new native char edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native char edge attribute map.
     */
    public CharEdgeMap createCharEdgeMap(Object key);

    /**
       Create a new native byte edge attribute map associated with a key.
       @param key the key of the new map.
       @return a managed native byte edge attribute map.
     */
    public ByteEdgeMap createByteEdgeMap(Object key);

    /**
       Create an unmanaged, ad-hoc generic node attribute map with
       default value <tt>null</tt>.
       
       @return an <em>unmanaged</em> attribute map for the nodes in 
       this graph.
     */
    public <T> NodeMap<T> createNodeMap();

    /**
       Create an unmanaged generic edge attribute map
       with default value <tt>null</tt>.
     */
    public <T> EdgeMap<T> createEdgeMap();

    /**
       Create an unmanaged, ad-hoc generic node attribute matrix
       with default value <tt>null</tt>.

       @return an <em>unmanaged</em> attribute matrix for the nodes in 
       this graph.
     */
    public <T> NodeMatrix<T> createNodeMatrix();

    /**
       Create a new unmanaged native int node attribute map.
       @return an unmanaged native int node attribute map.
     */
    public IntNodeMap createIntNodeMap();
    /**
       Create a new unmanaged native int node attribute matrix.
       @return an unmanaged native int node attribute matrix.
     */
    public IntNodeMatrix createIntNodeMatrix();
    /**
       Create an unmanaged new native float node attribute map.
       @return an unmanaged native float node attribute map.
     */
    public FloatNodeMap createFloatNodeMap();
    /**
       Create a new unmanaged native float node attribute matrix.
       @return an unmanaged native float node attribute matrix.
     */
    public FloatNodeMatrix createFloatNodeMatrix();

    /**
       Create a new native long node attribute map.
       @return an unmanaged native long node attribute map.
     */
    public LongNodeMap createLongNodeMap();

    /**
       Create a new unmanaged native long node attribute matrix.
       @return an unmanaged native long node attribute matrix.
     */
    public LongNodeMatrix createLongNodeMatrix();

    /**
       Create a new native double node attribute.
       @return an unmanaged native double node attribute map.
     */
    public DoubleNodeMap createDoubleNodeMap();
    /**
       Create a new unmanaged native double node attribute matrix.
       @return an unmanaged native double node attribute matrix.
     */
    public DoubleNodeMatrix createDoubleNodeMatrix();
    /**
       Create a new native char node attribute map.
       @return an unmanaged native char node attribute map.
     */
    public CharNodeMap createCharNodeMap();
    /**
       Create a new unmanaged native char node attribute matrix.
       @return an unmanaged native char node attribute matrix.
     */
    public CharNodeMatrix createCharNodeMatrix();

    /**
       Create a new native byte node attribute map.
       @return an unmanaged native byte node attribute map.
     */
    public ByteNodeMap createByteNodeMap();
    /**
       Create a new unmanaged native byte node attribute matrix.
       @return an unmanaged native byte node attribute matrix.
     */
    public ByteNodeMatrix createByteNodeMatrix();


    /**
       Create a new native int edge attribute map.
       @return an unmanaged native int edge attribute map.
     */
    public IntEdgeMap createIntEdgeMap();

    /**
       Create a new native float edge attribute map.
       @return an unmanaged native float edge attribute map.
     */
    public FloatEdgeMap createFloatEdgeMap();

    /**
       Create a new native long edge attribute map.
       @return an unmanaged native long edge attribute map.
     */
    public LongEdgeMap createLongEdgeMap();

    /**
       Create a new native double edge attribute map.
       @return an unmanaged native double edge attribute map.
     */
    public DoubleEdgeMap createDoubleEdgeMap();

    /**
       Create a new native char edge attribute map.
       @return an unmanaged native char edge attribute map.
     */
    public CharEdgeMap createCharEdgeMap();

    /**
       Create a new native byte edge attribute map.
       @return an unmanaged native byte edge attribute map.
     */
    public ByteEdgeMap createByteEdgeMap();

    /**
       remove the node map (native or not, matrix or not) 
       associated with the key <tt>key</tt>.
     */
    public void removeNodeMap(Object key);
    /**
       remove the edge map (native or not) 
       associated with the key <tt>key</tt>
     */
    public void removeEdgeMap(Object key);

    /**
       Register a special node by key.

       @param key the key of the distinguished node.
       @param n the node to distinguish.
     */
    public void registerNode(Object key, Node n);

    /**
       Unregister a special node.

       @param key the key of the node to be unregistered.
     */
    public void unregisterNode(Object key);

    /**
       Register a special edge by key.

       @param key the key of the distinguished edge.
       @param e the edge to distinguish.
     */
    public void registerEdge(Object key, Edge e);

    /**
       Unregister a special edge.

       @param key the key of the edge to be unregistered.
     */
    public void unregisterEdge(Object key);
}
