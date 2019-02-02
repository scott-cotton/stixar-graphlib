package stixar.graph.check;

import stixar.graph.Digraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.DFS;

import java.util.Arrays;

/**
   Check whether or not a digraph is a tree.

   @see DigraphProperty#Tree
 */
public class TreeChecker extends ForestChecker
    implements DigraphChecker
{
    protected Node root;

    public TreeChecker()
    {
        super();
        root = null;
    }

    public boolean check(Digraph dg)
    {
        root = null;
        if (!super.check(dg)) { // its not even a forest
            return false;
        }
        // check for unique root.
        int rootCount = 0;
        int nsz = dg.nodeSize();
        for(int i=0; i <nsz; ++i) {
            if (parents[i] == null) {
                if (++rootCount > 1) {
                    isTree = false;
                    return false;
                }
                root = digraph.node(i);
            }
        }
        // it's a forest so there can't be a cycle so root can't be null.
        assert root != null;
        return true;
    }

    public Node root()
    {
        return root;
    }

}
