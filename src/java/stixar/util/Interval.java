package stixar.util;

/**
   A simple interval class.
   
   <h3>Warning</h3> this class is add hoc and not fully thought 
   out.  It is likely the interface may change in the near future.
 */
public class Interval
{
    protected double lower;
    protected double upper;

    public Interval(double l, double u)
    {
        this.lower = l;
        this.upper = u;
    }

    public Interval merge(Interval i)
    {
        return new Interval(Math.min(i.lower, lower),
                            Math.max(i.upper, upper));
    }

    public final Interval intersect(Interval i)
    {
        return new Interval(Math.max(i.lower, lower), 
                            Math.min(i.upper, upper));
    }

    public final boolean equals(Interval i)
    {
        return Math.abs(lower - i.lower) < Precision.Epsilon
            &&
            Math.abs(upper - i.upper) < Precision.Epsilon;
    }

}
