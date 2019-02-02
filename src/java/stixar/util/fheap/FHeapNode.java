package stixar.util.fheap;

class FHeapNode<E> extends FHeapCell<E>
{

    static int top = 0;

    boolean mark;
    int degree;

    FHeapNode<E> parent;
    FHeapNode<E> child;
    FHeapNode<E> left;
    FHeapNode<E> right;

    public FHeapNode(E elt)
    {
        super(elt);
        parent = null;
        child = null;
        left = this;
        right = this;
        degree = 0;
        mark = false;
    }

    public String toString()
    {
        return "[e=" + value() + ", m=" + mark + ", d=" + degree  + "]";
    }
}
