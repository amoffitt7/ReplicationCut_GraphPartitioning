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
    private ArrayList<Integer> sAndT;
 
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
        sAndT = new ArrayList<Integer>();
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
 
    public int  maxFlowMinCut (int graph[][], int source, int destination)
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
        
        // return S, R, and T
        // S is the reachable vertices in the original graph
        System.out.print("S: {");
        for (int i = 0; i < reachable.size(); i++) {
            if (reachable.get(i) <= oldNumberOfVertices) {
                System.out.print(reachable.get(i) + " ");
                sAndT.add(reachable.get(i));
            }
        }
        System.out.print("}\n");

        // T is the unreachable vertices in the prime graph
        System.out.print("T: {");
        for (int i = 0; i < unreachable.size(); i++) {
            if (unreachable.get(i) > oldNumberOfVertices && unreachable.get(i) <= (numberOfVertices - 2)) {
                Integer toAdd = unreachable.get(i) - oldNumberOfVertices;
                System.out.print(toAdd + " ");
                sAndT.add(toAdd);
            }
        }
        System.out.print("}\n");

        // R is the vertices not in S or T
        System.out.print("R: {");
        for (int i = 1; i <= oldNumberOfVertices; i++) {
            if (!sAndT.contains(new Integer(i))) {
                System.out.print(i + " ");
            }
        }
        System.out.print("}\n");


        return maxFlow;
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
