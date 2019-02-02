package stixar.graph.check;

import stixar.graph.UGraph;

/**
   Interface for a property checker of undirected graphs.
 */
public interface UGraphChecker
{
    public boolean check(UGraph graph);
}
