package stixar.graph.check;

import stixar.graph.UGraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.DFS;

import java.util.Arrays;

/**
   Checks whether or not an undirected graph is a forest.

   @see UGraphProperty#Forest
 */
public class UForestChecker extends DFS.Visitor
    implements UGraphChecker
{
    protected enum EdgeStat { seen, unseen }
    protected EdgeStat[] eStats;
    protected Edge[] parents;
    protected boolean falsified;

    public UForestChecker()
    {
        parents = new Edge[512];
        eStats = new EdgeStat[1024];
        falsified = false;
    }

    protected void reset(UGraph g)
    {
        int esz = g.edgeSize();
        if (eStats.length < esz) {
            eStats = new EdgeStat[esz];
        }
        int nsz = g.nodeSize();
        if (parents.length < nsz) {
            parents = new Edge[nsz];
        }
        Arrays.fill(eStats, EdgeStat.unseen);
        Arrays.fill(parents, null);
        falsified = false;
    }

    public boolean check(UGraph g)
    {
        if (g.edgeSize() >= g.nodeSize()) return false;
        reset(g);
        DFS dfs = new DFS(g, this);
        dfs.run();
        return falsified;
    }

    public void backEdge(Edge e)
    {
        falsified |= e.get(eStats) == EdgeStat.seen;
    }

    public void treeEdge(Edge e)
    {
        e.set(eStats, EdgeStat.seen);
        e.target().set(parents, e);
    }

    public Edge[] parents()
    {
        return parents;
    }

    public boolean done()
    {
        return falsified;
    }
}
