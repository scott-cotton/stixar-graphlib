package stixar.util;

import java.io.Serializable;
/**
   A basic Pair class.
 */
public class Pair<A,B> implements Serializable
{
    /** the first element */
    public A first;
    /** the second element */
    public B second;

    /** Construct a pair from the first and second elements. */
    public Pair(A a, B b) 
    {
	first = a;
	second = b;
    }
    
    /** Construct a pair with null first and second elements. */
    public Pair()
    {
	first = null;
	second = null;
    }

    /** return the first element of the pair */
    public final A first() { return first; }
    /** return the second element of the pair */
    public final B second() { return second; }
    /** set the first element of the pair */
    public void first(A a) { first = a; }
    /** set the second element of the pair */
    public void second(B b) { second = b; }

    /**
       Compute the hash code for this pair.
     */
    public int hashCode()
    {
	return (first == null ? 0 : first.hashCode()) + 
            7 * (second == null ? 0 : second.hashCode());
    }

    /**
       Human readable form.
     */
    public String toString()
    {
	return "Pair(" + first + ", " + second + ")";
    }

    /**
       Test equality by parts.
     */
    public boolean equals(Object o)
    {
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            if (first != null && !first.equals(p.first)) return false;
            if (first == null && p.first != null) return false;
            if (second != null && !second.equals(p.second)) return false;
            if (second == null && p.second != null) return false;
            return true;
        } else {
            return false;
        }
    }

}


