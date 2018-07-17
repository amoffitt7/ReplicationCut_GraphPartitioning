/* Contains the algorithm. 
 * Needs MaxFlowMinCut.java and Cut.java to run.
 */

import java.io.*;
import java.util.*;

public class ReplicationCutAlgorithm {

    private Set<Cut> minCuts;
    private int numberOfDistinctMinCuts;
    private int numberOfVertices;
    private String fileName;
    private String reportFileName;
    private File file;
    private PrintWriter reportpw;
    private HashMap<Integer, Integer> sSizeMap; // keeps track of the number of nodes in S in each cut
    private int[][] adjMatrix;
    private int newVertices;
    private static Integer myInf = Integer.MAX_VALUE;
    private int supersource; 
    private int supersink;


    public ReplicationCutAlgorithm(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
        minCuts = new HashSet<Cut>();
        String[] fileNameParts = file.getAbsolutePath().split("\\.");
        reportFileName = fileNameParts[0] + "_report.txt";

        sSizeMap = new HashMap<Integer, Integer>();
    }

    // Has the precondition that runAlgorithm has been called
    public int getDistinctNumberOfMinCuts() {
        return numberOfDistinctMinCuts;
    }

    // has the precondition that runAlgorithm has been called
    public HashMap<Integer, Integer> getSSizeMap() {
        return sSizeMap;    
    }

    public void runAlgorithm() {
        createReplicationGraphAllPairs();
        recordResults();
    }

    // function to create replication graph for all pairs
    private void createReplicationGraphAllPairs() {
    	//System.out.println("In all pairs");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            // get the number of vertices in this graph from the file
            int vertices = 0;
            String header;
            if ((header = br.readLine()) != null) {
                header = header.trim(); // get rid of leading whitespace
                vertices = Integer.parseInt(header); 
            }

            if (vertices < 2) {
                System.err.println("Error: Not enough vertices");
                br.close();
                return;
            }

            this.numberOfVertices = vertices;
            
            createBaseAdjacenecyMatrix();

            // creates replication graph for all C(n,2) pair combinations
            for (int i = 1; i <= numberOfVertices; i++) {
                for (int j = i+1; j <= numberOfVertices; j++) {
                    createReplicationGraph(i, j);
                }
            }
            br.close();
        }
        catch (IOException e) {
            System.err.println("Error reading file");
        }
    }
    
    
    
    private void createBaseAdjacenecyMatrix() {
    	newVertices = numberOfVertices * 2 + 2;
    	adjMatrix = new int[newVertices + 1][newVertices + 1]; 
    	supersource = newVertices - 1;  /* the supersource will take the second to last index */
        supersink = newVertices;        /* supersink will take the last index */
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            // get the header out of the way
            if ((br.readLine()) == null) {
            	System.err.println("First Line ERROR");
            }

            // Parse the edges. Duplicate them but reverse direction
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                nextLine = nextLine.trim();
                String[] entries = nextLine.split("\\s+"); // splits into 3 numbers
                
                int firstVertex = Integer.parseInt(entries[0]);
                int secondVertex = Integer.parseInt(entries[1]);
                int edgeWeight = Integer.parseInt(entries[2]);

                // creates new vertices using old ones and reverses direction
                int newFirstVertex = secondVertex + numberOfVertices; 
                int newSecondVertex = firstVertex + numberOfVertices;
                
                adjMatrix[firstVertex][secondVertex] = edgeWeight;
                adjMatrix[newFirstVertex][newSecondVertex] = edgeWeight;
            }
            
            br.close();
        }
        catch (IOException e) {
            System.err.println("Error reading file");
        }
    }

    
    
    // function to create replication graph for a specified pair
    private void createReplicationGraph(int s, int t) { 
    	try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));
            //System.out.println("In single pair. Finding the cut between " + s + " and " + t + ":");
            reportpw.println("Finding the cut between " + s + " and " + t + ":");
            reportpw.close();
        } catch (IOException e) {
            System.err.println("Error");
        }
        
        setSupers(s, t);
        setMiddleNodes(s, t);

        runFlowAlgorithm(adjMatrix, supersource, supersink, newVertices);
        
        unSetSupers(s, t);
        unSetMiddleNodes(s, t);
    }
    
    
    
    void setSupers(int s, int t) {
    	//add the edges from supersource (s*) to sources (s and s')
        adjMatrix[supersource][s] = myInf;
        adjMatrix[supersource][s + numberOfVertices] = myInf;
        
        //add the edges from sinks (t and t') to supersink 
        adjMatrix[t][supersink] = myInf;
        adjMatrix[t + numberOfVertices][supersink] = myInf; 
    }
    
    
    
    void setMiddleNodes(int s, int t) {
    	// for all vertices that are not source and sink, add infinite paths from vertices to their primes
        for (int i = 1; i <= numberOfVertices; i++) {
            if (i != s && i != t) {
                int iPrime = i + numberOfVertices;
                adjMatrix[i][iPrime] = myInf; 
            }
        }
    }
    
    
    
    void unSetSupers(int s, int t) {
    	//remove the edges from supersource (s*) to sources (s and s')
        adjMatrix[supersource][s] = 0;
        adjMatrix[supersource][s + numberOfVertices] = 0;
        
        //remove the edges from sinks (t and t') to supersink 
        adjMatrix[t][supersink] = 0;
        adjMatrix[t + numberOfVertices][supersink] = 0; 
    }
    
    
    
    void unSetMiddleNodes(int s, int t) {
    	// for all vertices that are not source and sink, add infinite paths from vertices to their primes
        for (int i = 1; i <= numberOfVertices; i++) {
            if (i != s && i != t) {
                int iPrime = i + numberOfVertices;
                adjMatrix[i][iPrime] = 0; 
            }
        }
    }
    
    
    
    private void runFlowAlgorithm(int[][] adjMatrix, int source, int sink, int numVertices) {
    	int[][] graph;
        int numberOfNodes;
        Cut minCut;
 
        numberOfNodes = numVertices;
        graph = adjMatrix;
 
        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(numberOfNodes);
        minCut = maxFlowMinCut.maxFlowMinCut(graph, source, sink);

        
        
        // store info about whether this mincut is distinct by adding it to a set which doesn't allow duplicates
        boolean newCut = true;
        for(Cut c: minCuts) {
        	if(c.equals(minCut)) { newCut = false; }
        }
        if (newCut && minCuts.add(minCut)) {
            //System.out.println("\tNew distinct mincut found!");
            //reportpw.println("*** New distinct mincut found! ***");
            // if the cut is new, store info about the cut's S 
            Integer sSize = new Integer(minCut.S.size()); // key is the size of s
            if (!sSizeMap.containsKey(sSize)) {
                sSizeMap.put(sSize, new Integer(1)); // if key doesn't exist yet, set its value to 1
            }
            else {
                sSizeMap.put(sSize, sSizeMap.get(sSize) + 1); // if key already exists, increment its value
            }
        }
        
        try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));
            //System.out.println(minCut + "\n");    
            reportpw.println(minCut + "\n"); 
            reportpw.close();
        } catch (IOException e) {
            System.err.println("Error");
        }
        
    }
    
    // Creates a report file and also sets the number of distinct min cuts (in the future can split these two functions)
    private void recordResults() {
        //ANALYZE RESULTS FOR EACH GRAPH
        try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));

            numberOfDistinctMinCuts = minCuts.size(); 

            /*
            System.out.println("****************");
            System.out.println("FINAL REPORT:");
            System.out.println("****************");

            System.out.println("The file was " + fileName + ".");
            System.out.println("The graph had " + numberOfVertices + " vertices.");
            System.out.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            System.out.println("Note: The actual number may be lower due to equivalent cuts.");
            System.out.println("Check the report file for more information.\n");
            */

            reportpw.println("****************");
            reportpw.println("FINAL REPORT:");
            reportpw.println("****************");

            reportpw.println("The file was " + fileName + ".");
            reportpw.println("The graph had " + numberOfVertices + " vertices.");
            reportpw.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            reportpw.println("Note: The actual number may be lower due to equivalent cuts.\n");

            reportpw.close();
        }
        catch (IOException e) {
            System.err.println("ERROR");
        }

        // mark if it's n choose 2
        int nChooseTwo = numberOfVertices * (numberOfVertices - 1) / 2;
        if (numberOfDistinctMinCuts == nChooseTwo) {
            File file = new File(reportFileName); // old name
            String[] fileNameParts = reportFileName.split("\\.");
            String newReportFileName = fileNameParts[0] + "_GOOD.txt";
            File file2 = new File(newReportFileName); // new name
            file.renameTo(file2); // Rename file
            reportFileName = newReportFileName;
        }
    }
	
}
