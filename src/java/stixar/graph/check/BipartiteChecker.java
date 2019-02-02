package stixar.graph.check;

import stixar.graph.UGraph;
import stixar.graph.Node;
import stixar.graph.Edge;

import stixar.graph.search.BFS;

import java.util.Arrays;

/**
   Checks whether or not an undirected graph is bipartite.
   @see UGraphProperty#Bipartite
 */
public class BipartiteChecker extends BFS.Visitor 
    implements UGraphChecker
{

    protected enum EdgeStatus { seen, unseen; }

    /**
       partition names in a bipartite graph.
     */
    public enum Partition { one, two }

    protected BFS bfs;
    protected EdgeStatus[] eStats;
    protected boolean falsified;
    protected Partition[] partition;
    protected UGraph graph;

    public BipartiteChecker()
    {
        eStats = new EdgeStatus[1024];
        partition = new Partition[1024];
        falsified = false;
        
    }

    public boolean check(UGraph graph)
    {
        reset(graph);
        BFS bfs = new BFS(graph, this);
        bfs.run();
        return !falsified;
    }

    protected void reset(UGraph g)
    {
        graph = g;
        int esz = g.edgeAttrSize();
        if (eStats.length < esz)
            eStats = new EdgeStatus[esz];
        if (partition.length < g.nodeAttrSize()) 
            partition = new Partition[Math.max(partition.length * 2, g.nodeAttrSize())];
        Arrays.fill(partition, Partition.one);
        Arrays.fill(eStats, 0, esz, EdgeStatus.unseen);
        falsified = false;
    }

    public void queueEdge(Edge e)
    {
        if (e.get(eStats) == EdgeStatus.seen)
            return;
        falsified = true;
    }

    public void treeEdge(Edge e)
    {
        if (e.get(eStats) == EdgeStatus.seen)
            return;
        e.set(eStats, EdgeStatus.seen);
        anyEdge(e);
    }

    public void crossEdge(Edge e)
    {
        if (e.get(eStats) == EdgeStatus.seen)
            return;
        e.set(eStats, EdgeStatus.seen);
        anyEdge(e);
    }

    protected void anyEdge(Edge e)
    {
        Node s = e.source();
        Node t = e.target();
        switch (s.get(partition)) {
        case one:
            t.set(partition, Partition.two);
            break;
        case two:
            t.set(partition, Partition.one);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    /**
       Return the partition from the last check if it exists.
       <p>
       Returns an attribute array of partitions for the nodes in the
       graph.
       </p>
     */
    public Partition[] partition()
    {
        Partition[] res = new Partition[graph.nodeAttrSize()];
        System.arraycopy(partition, 0, res, 0, graph.nodeAttrSize());
        return res;
    }

    public boolean done()
    {
        return falsified;
    }
}
