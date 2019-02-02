
package stixar.graph;

/**
   All graph algorithms have this interface.
 */
public interface Algorithm extends Runnable
{

    /**
       This method executes the algorithm.
     */
    public void run();

}
