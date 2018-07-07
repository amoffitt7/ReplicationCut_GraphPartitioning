/*
 * Main file for testing code
 * 
 */
import java.io.*;

public class Run_Test {

	// function to create replication graph for all pairs
    public static void CreateReplicationGraphAllPairs(File file) {
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

            // creates replication graph for all C(n,2) pair combinations
            for (int i = 1; i <= vertices; i++) {
                for (int j = i+1; j <= vertices; j++) {
                    CreateReplicationGraph(file, i, j);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    
    
    // function to create replication graph for a specified pair
    public static void CreateReplicationGraph(File file, int s, int t) { 
    	System.out.println("In single pair");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String[] fileNameParts = file.getName().split("\\.");
            String repGraphFileName = fileNameParts[0] + "_" + s + "_" + t + ".txt";
            //BufferedWriter bw = new BufferedWriter(new FileWriter(repGraphFileName));
            PrintWriter pw = new PrintWriter(new FileWriter(repGraphFileName));

            int vertices = 0;
            int newVertices = 0;
            //int edges = 0;
            Integer myInf = Integer.MAX_VALUE;

            // get the header
            String header;
            if ((header = br.readLine()) != null) {
                header = header.trim(); // get rid of leading whitespace
                String[] entries = header.split("\\s+"); // splits the rest into entries
                vertices = Integer.parseInt(entries[0]); // first entry is number of vertices
                //edges = Integer.parseInt(entries[1]); // second entry is number of edges

                // this is the number of vertices in the replication graph; 
                // all the original vertices duplicated plus super source and super sink
                newVertices = vertices * 2 + 2; 
                pw.format("%5d\r\n", newVertices);
            }

            if (vertices < 2) {
                System.err.println("Error: Not enough vertices");
                pw.close();
                return;
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
                int newFirstVertex = secondVertex + vertices; 
                int newSecondVertex = firstVertex + vertices;

                pw.format("%5d   %5d   %5d \r\n", firstVertex, secondVertex, edgeWeight); // add the original edge
                pw.format("%5d   %5d   %5d \r\n", newFirstVertex, newSecondVertex, edgeWeight); // add the new edge
            }

            int supersource = newVertices - 1; // the supersource will take the second to last index
            int supersink = newVertices; // supersink will take the last index

            int sPrime = s + vertices;
            int tPrime = t + vertices;

            // add the edges from supersource (s*) to sources (s and s')
            pw.format("%5d   %5d   %5d \r\n", supersource, s, myInf); 
            pw.format("%5d   %5d   %5d \r\n", supersource, sPrime, myInf); 

            // add the edges from sinks (t and t') to supersink 
            pw.format("%5d   %5d   %5d \r\n", t, supersink, myInf); 
            pw.format("%5d   %5d   %5d \r\n", tPrime, supersink, myInf); 

            // for all vertices that are not source and sink, add infinite paths from vertices to their primes
            for (int i = 1; i <= vertices; i++) {
                if (i != s && i != t) {
                    int iPrime = i + vertices;
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
    
    
    
    public static void createAdjacencyMatrix(String filename, int s, int t, int numVertices) {
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
    
    
    
    public static void runFlowAlgorithm(int[][] adjMatrix, int source, int sink, int numVertices) {
    	int[][] graph;
        int numberOfNodes;
        int maxFlow;
 
        numberOfNodes = numVertices;
        graph = adjMatrix;
 
        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(numberOfNodes);
        maxFlow = maxFlowMinCut.maxFlowMinCut(graph, source, sink);
 
        System.out.println("The Max Flow is " + maxFlow);
        System.out.println("The Cut Set is ");
        maxFlowMinCut.printCutSet();
        
        recordResults();
    }
    
    
    
    public static void recordResults() {
    	//ANALYZE RESULTS FOR EACH GRAPH
    }
	
    
    
	public static void main(String[] args) {
		//PULL FILES FROM STORED LOCATION
		File file = new File(args[0]);
		System.out.println("Argument string = " + args[0]);
		
		CreateReplicationGraphAllPairs(file);
	}
}





