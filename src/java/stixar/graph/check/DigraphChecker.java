package stixar.graph.check;

import stixar.graph.Digraph;

/**
   Interface for a property checker of digraphs.
 */
public interface DigraphChecker
{
    public boolean check(Digraph digraph);
}
