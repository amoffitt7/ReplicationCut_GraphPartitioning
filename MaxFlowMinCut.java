/* Max Flow Min Cut algorithm implementation by Sanfoundry
 * https://www.sanfoundry.com/java-program-implement-max-flow-min-cut-theorem/
 */ 

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
 
public class MaxFlowMinCut
{
    private int[] parent;
    private Queue<Integer> queue;
    private int numberOfVertices;
    private int oldNumberOfVertices;
    private boolean[] visited;
    private Set<Pair> cutSet;
    private ArrayList<Integer> reachable;
    private ArrayList<Integer> unreachable;
    private ArrayList<Integer> S;
    private ArrayList<Integer> T;
    private ArrayList<Integer> R;
 
    public MaxFlowMinCut (int numberOfVertices)
    {
        this.numberOfVertices = numberOfVertices;
        oldNumberOfVertices = (numberOfVertices - 2) / 2;
        this.queue = new LinkedList<Integer>();
        parent = new int[numberOfVertices + 1];
        visited = new boolean[numberOfVertices + 1];
        cutSet = new HashSet<Pair>();
        reachable = new ArrayList<Integer>();
        unreachable = new ArrayList<Integer>();
        S = new ArrayList<Integer>();
        T = new ArrayList<Integer>();
        R = new ArrayList<Integer>();
    }
 
    public boolean bfs (int source, int goal, int graph[][])
    {
        boolean pathFound = false;
        int destination, element;
        for (int vertex = 1; vertex <= numberOfVertices; vertex++)
        {
            parent[vertex] = -1;
            visited[vertex] = false;
        }
        queue.add(source);
        parent[source] = -1;
        visited[source] = true;
 
        while (!queue.isEmpty())
        {
            element = queue.remove();
            destination = 1;
            while (destination <= numberOfVertices)
            {
                if (graph[element][destination] > 0 &&  !visited[destination])
                {
                    parent[destination] = element;
                    queue.add(destination);
                    visited[destination] = true;
                }
                destination++;
            }
        }
 
        if (visited[goal])
        {
            pathFound = true;
        }
        return pathFound;
    }
 
    public Cut maxFlowMinCut (int graph[][], int source, int destination)
    {
        //System.err.println("Currently running max flow min cut on " + source + " to " + destination);
        int u, v;
        int maxFlow = 0;
        int pathFlow;
        int[][] residualGraph = new int[numberOfVertices + 1][numberOfVertices + 1];
 
        for (int sourceVertex = 1; sourceVertex <= numberOfVertices; sourceVertex++)
        {
            for (int destinationVertex = 1; destinationVertex <= numberOfVertices; destinationVertex++)
            {
                residualGraph[sourceVertex][destinationVertex] = graph[sourceVertex][destinationVertex];
            }
        }
 
        /*max flow*/
        while (bfs(source, destination, residualGraph))
        {
            pathFlow = Integer.MAX_VALUE;
            for (v = destination; v != source; v = parent[v])
            {
                u = parent[v];
                pathFlow = Math.min(pathFlow,residualGraph[u][v]);
                //System.out.println("\tPath flow is " + pathFlow);
            }
            for (v = destination; v != source; v = parent[v])
            {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }
            maxFlow += pathFlow;	
            //System.out.println("\tMax flow is " + maxFlow);
        }
 
        /*calculate the cut set*/		
        for (int vertex = 1; vertex <= numberOfVertices; vertex++)
        {
            if (bfs(source, vertex, residualGraph))
            {
                reachable.add(vertex);
            }
            else
            {
                unreachable.add(vertex);
            }
        }
        for (int i = 0; i < reachable.size(); i++)
        {
            for (int j = 0; j < unreachable.size(); j++)
            {
                if (graph[reachable.get(i)][unreachable.get(j)] > 0)
                {
                    cutSet.add(new Pair(reachable.get(i), unreachable.get(j)));
                    //System.err.println("\tAdding " + reachable.get(i) + ", " + unreachable.get(j));
                }
            }
        }
        
        // get S, R, and T
        // S is the reachable vertices in the original graph
        for (int i = 0; i < reachable.size(); i++) {
            if (reachable.get(i) <= oldNumberOfVertices) {
                S.add(reachable.get(i));
            }
        }

        // T is the unreachable vertices in the prime graph
        for (int i = 0; i < unreachable.size(); i++) {
            if (unreachable.get(i) > oldNumberOfVertices && unreachable.get(i) <= (numberOfVertices - 2)) {
                // the true, original vertex is the prime vertex minus the old number of vertices
                Integer trueVertex = unreachable.get(i) - oldNumberOfVertices;
                T.add(trueVertex);
            }
        }

        // R is the vertices which are not in S or T
        for (int i = 1; i <= oldNumberOfVertices; i++) {
            if ((!S.contains(new Integer(i))) && (!T.contains(new Integer(i)))) {
                R.add(new Integer(i));
            }
        }

        Cut minCut = new Cut(maxFlow, S, T, R);

        return minCut;
    }
    
    
 
    public void printCutSet ()
    {
        Iterator<Pair> iterator = cutSet.iterator();
        while (iterator.hasNext())
        {
            Pair pair = iterator.next();
            System.out.println(pair.source + "-" + pair.destination);
        }
    }
}
 


class Pair
{
    public int source;
    public int destination;
 
    public Pair (int source, int destination)
    {
        this.source = source;
        this.destination = destination;
    }
 
    public Pair()
    {
    }
}
