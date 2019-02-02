package stixar.util;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;

/**
   An adaptor interface for making parametric attributes work with
   shortests paths algorithms.
   <p>
   This interface defines what is necessary for a attributes of
   arbitrary types to participate in a shortest path algorithm.  An 
   adaptor <tt>NumAdaptor&lt;T&gt;</tt> adapts an arbitrary class
   T to play the role of path weights in shortest paths problems.
   </p>
   @see #Long 
   @see #Float 
   @see #Int 
   @see #Double
 */
public interface NumAdaptor<T>
    extends Comparator<T>
{
    /**
       Add together two path weights and return the result
       @param v1 the first path weight.
       @param v2 the second path weight.
       @return the result of adding together the two path weights, which
       should always be less than {@link #inf} when summing all the edge
       weights in a given graph.
     */
    public T add(T v1, T v2);

    public T subtract(T v1, T v2);

    /**
       Return the zero value for the type <tt>T</tt>.
     */
    public T zero();
    
    /**
       Returns the maximum, infinity, or positive infinity value
       for the type <tt>T</tt>.
       <p>
       The returned value <tt>inf</tt> should have the property that it is 
       greater than every over value of type <tt>T</tt>, and
       that every sum of <tt>|E|</tt> values of type <tt>T</tt> in a given 
       graph with <tt>|E|</tt> edges gives a value less than  <tt>inf</tt>.
       </p>
       @return an infinity value.
    */
    public T inf();

    public T minusInf();
    
    /**
       Adaptor for shortests paths algorithms for Integer attributes.
     */
    public static final NumAdaptor<Integer> Int =
        new NumAdaptor<Integer>()
    {

        public final Integer add(Integer i1, Integer i2)
        {
            return i1.intValue() + i2.intValue();
        }

        public final Integer subtract(Integer i1, Integer i2)
        {
            return i1.intValue() - i2.intValue();
        }

        public final Integer zero() 
        {
            return 0;
        }

        public final Integer inf()
        {
            return Integer.MAX_VALUE;
        }

        public final Integer minusInf()
        {
            return Integer.MIN_VALUE;
        }

        public final int compare(Integer i1, Integer i2)
        {
            int ii1 = i1;
            int ii2 = i2;
            return ii1 - ii2;
        }
    };


    /**
       Adaptor for shortests paths algorithms for Float attributes.
     */
    public static final NumAdaptor<java.lang.Float> Float
        = new NumAdaptor<java.lang.Float>()
    {

        public final java.lang.Float add(java.lang.Float i1, java.lang.Float i2)
        {
            return i1 + i2;
        }

        public final java.lang.Float subtract(java.lang.Float f1, java.lang.Float f2)
        {
            return f1 - f2;
        }

        public final java.lang.Float zero() 
        {
            return 0f;
        }

        public final java.lang.Float inf()
        {
            return java.lang.Float.POSITIVE_INFINITY;
        }
        
        public final java.lang.Float minusInf()
        {
            return java.lang.Float.NEGATIVE_INFINITY;
        }

        public final int compare(java.lang.Float i1, java.lang.Float i2)
        {
            float ii1 = i1;
            float ii2 = i2;
            return ii1 < ii2 ? -1 : (ii1 == ii2 ? 0 : 1);
        }
    };


    /**
       Adaptor for shortests paths algorithms for <tt>Long</tt> attributed
       graphs.
     */
    public static final NumAdaptor<java.lang.Long> Long
        = new NumAdaptor<java.lang.Long>()
    {

        public final java.lang.Long add(java.lang.Long i1, java.lang.Long i2)
        {
            return i1.longValue() + i2.longValue();
        }

        public final java.lang.Long subtract(java.lang.Long l1, java.lang.Long l2)
        {
            return l1.longValue() - l2.longValue();
        }

        public final java.lang.Long zero()
        {
            return 0L;
        }

        public final java.lang.Long inf()
        {
            return java.lang.Long.MAX_VALUE;
        }

        public final java.lang.Long minusInf()
        {
            return java.lang.Long.MIN_VALUE;
        }

        public final int compare(java.lang.Long i1, java.lang.Long i2)
        {
            long ii1 = i1;
            long ii2 = i2;
            return (int) (ii1 - ii2);
        }
    };


    /**
       Adaptor for shortests paths algorithms for <tt>Double</tt> attributes.
       graphs.
     */
    public static final NumAdaptor<java.lang.Double> Double
        = new NumAdaptor<java.lang.Double>()
    {

        public final java.lang.Double add(java.lang.Double i1, java.lang.Double i2)
        {
            return i1.doubleValue() + i2.doubleValue();
        }

        public final java.lang.Double subtract(java.lang.Double d1, java.lang.Double d2)
        {
            return d1.doubleValue() - d2.doubleValue();
        }

        public final java.lang.Double zero()
        {
            return 0d;
        }

        public final java.lang.Double inf()
        {
            return java.lang.Double.POSITIVE_INFINITY;
        }

        public final java.lang.Double minusInf()
        {
            return java.lang.Double.NEGATIVE_INFINITY;
        }

        public final int compare(java.lang.Double i1, java.lang.Double i2)
        {
            double ii1 = i1;
            double ii2 = i2;
            return ii1 < ii2 ? -1 : (ii1 == ii2 ? 0 : 1);
        }
    };
}
