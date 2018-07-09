/* Contains the algorithm. 
 * Needs MaxFlowMinCut.java and Cut.java to run.
 */

import java.io.*;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class ReplicationCutAlgorithm {

    private Set<Cut> minCuts;
    private int numberOfDistinctMinCuts;
    private int numberOfVertices;
    private String fileName;
    private File file;

    public ReplicationCutAlgorithm(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
        minCuts = new HashSet<Cut>();
    }

    public void findDistinctNumberOfMinCuts() {
        CreateReplicationGraphAllPairs();
    }

    // function to create replication graph for all pairs
    public void CreateReplicationGraphAllPairs() {
    	System.out.println("In all pairs");
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

            // creates replication graph for all C(n,2) pair combinations
            for (int i = 1; i <= numberOfVertices; i++) {
                for (int j = i+1; j <= numberOfVertices; j++) {
                    CreateReplicationGraph(i, j);
                }
            }
            recordResults();
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    
    
    // function to create replication graph for a specified pair
    public void CreateReplicationGraph(int s, int t) { 
    	System.out.println("In single pair. Finding the cut between " + s + " and " + t + ":");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String[] fileNameParts = file.getName().split("\\.");
            String repGraphFileName = fileNameParts[0] + "_" + s + "_" + t + ".txt";
            //BufferedWriter bw = new BufferedWriter(new FileWriter(repGraphFileName));
            PrintWriter pw = new PrintWriter(new FileWriter(repGraphFileName));

            Integer myInf = Integer.MAX_VALUE;

            // this is the number of vertices in the replication graph; 
            // all the original vertices duplicated plus super source and super sink
            int newVertices = numberOfVertices * 2 + 2;
            pw.format("%5d\r\n", newVertices);

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

                pw.format("%5d   %5d   %5d \r\n", firstVertex, secondVertex, edgeWeight); // add the original edge
                pw.format("%5d   %5d   %5d \r\n", newFirstVertex, newSecondVertex, edgeWeight); // add the new edge
            }

            int supersource = newVertices - 1; // the supersource will take the second to last index
            int supersink = newVertices; // supersink will take the last index

            int sPrime = s + numberOfVertices;
            int tPrime = t + numberOfVertices;

            // add the edges from supersource (s*) to sources (s and s')
            pw.format("%5d   %5d   %5d \r\n", supersource, s, myInf); 
            pw.format("%5d   %5d   %5d \r\n", supersource, sPrime, myInf); 

            // add the edges from sinks (t and t') to supersink 
            pw.format("%5d   %5d   %5d \r\n", t, supersink, myInf); 
            pw.format("%5d   %5d   %5d \r\n", tPrime, supersink, myInf); 

            // for all vertices that are not source and sink, add infinite paths from vertices to their primes
            for (int i = 1; i <= numberOfVertices; i++) {
                if (i != s && i != t) {
                    int iPrime = i + numberOfVertices;
                    pw.format("%5d   %5d   %5d \r\n", i, iPrime, myInf); 
                }
            }

            pw.close();
            createAdjacencyMatrix(repGraphFileName, supersource, supersink, newVertices);
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
    
    
    
    public void createAdjacencyMatrix(String filename, int s, int t, int numVertices) {
    	System.out.println("In matrix chunk");
    	File file = new File(filename);
    	int[][] adjMatrix = new int[numVertices + 1][numVertices + 1];
    	    	
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(file));
    		
    		// get the first line out of the way
            if ((br.readLine()) == null) {
            	System.err.println("First Line ERROR");
            }

            // Grab edge by edge
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                nextLine = nextLine.trim();
                String[] entries = nextLine.split("\\s+"); // splits into 3 numbers
                
                int firstVertex = Integer.parseInt(entries[0]);
                int secondVertex = Integer.parseInt(entries[1]);
                int edgeWeight = Integer.parseInt(entries[2]);

                adjMatrix[firstVertex][secondVertex] = edgeWeight;
            } 		

            /*
            // Printing the adjacency matrix
            // Loop through all rows
            for (int i = 0; i < adjMatrix.length; i++) {
                // Loop through all elements of current row
                for (int j = 0; j < adjMatrix[i].length; j++)
                    System.out.print(adjMatrix[i][j] + " ");
                System.out.println("");
            }*/

    	} catch(IOException e) {
    		System.err.println("Read ERROR");
    	}
    	
    	runFlowAlgorithm(adjMatrix, s, t, numVertices);
    }
    
    
    
    public void runFlowAlgorithm(int[][] adjMatrix, int source, int sink, int numVertices) {
    	int[][] graph;
        int numberOfNodes;
        Cut minCut;
        int maxFlow;
 
        numberOfNodes = numVertices;
        graph = adjMatrix;
 
        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(numberOfNodes);
        minCut = maxFlowMinCut.maxFlowMinCut(graph, source, sink);
        maxFlow = minCut.maxFlow;
        if (minCuts.add(minCut)) {
            System.out.println("\tNew distinct mincut found!");
        }
 
        System.out.println(minCut + "\n");  
        
    }
    
    
    
    public void recordResults() {
        //ANALYZE RESULTS FOR EACH GRAPH
        try {
            String[] fileNameParts = file.getName().split("\\.");
            String reportFileName = fileNameParts[0] + "_report.txt";
            PrintWriter pw = new PrintWriter(new FileWriter(reportFileName));

            numberOfDistinctMinCuts = minCuts.size(); 

            System.out.println("****************");
            System.out.println("FINAL REPORT:");
            System.out.println("****************");

            System.out.println("The file was " + fileName + ".");
            System.out.println("The graph had " + numberOfVertices + " vertices.");
            System.out.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            System.out.println("Note: The actual number may be lower due to equivalent cuts.\n");

            pw.println("****************");
            pw.println("FINAL REPORT:");
            pw.println("****************");

            pw.println("The file was " + fileName + ".");
            pw.println("The graph had " + numberOfVertices + " vertices.");
            pw.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            pw.println("Note: The actual number may be lower due to equivalent cuts.\n");

            pw.close();
        }
        catch (IOException e) {
            System.err.println("ERROR");
        }
    }
	
}