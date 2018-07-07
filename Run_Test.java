/*
 * Main file for code
 * 
 * Alisa, feel free to make separate class files if
 * you want
 * 
 */
import java.io.*;

public class Run_Test {

	// function to create replication graph for all pairs
    public static void CreateReplicationGraphAllPairs(File file) {
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
                pw.format("%5d\n", newVertices);
            }

            if (vertices < 2) {
                System.err.println("Error: Not enough vertices");
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

                pw.format("%5d   %5d   %5d\n", firstVertex, secondVertex, edgeWeight); // add the original edge
                pw.format("%5d   %5d   %5d\n", newFirstVertex, newSecondVertex, edgeWeight); // add the new edge
            }

            int supersource = newVertices - 1; // the supersource will take the second to last index
            int supersink = newVertices; // supersink will take the last index

            int sPrime = s + vertices;
            int tPrime = t + vertices;

            // add the edges from supersource (s*) to sources (s and s')
            pw.format("%5d   %5d   %5d\n", supersource, s, myInf); 
            pw.format("%5d   %5d   %5d\n", supersource, sPrime, myInf); 

            // add the edges from sinks (t and t') to supersink 
            pw.format("%5d   %5d   %5d\n", t, supersink, myInf); 
            pw.format("%5d   %5d   %5d\n", tPrime, supersink, myInf); 

            // for all vertices that are not source and sink, add infinite paths from vertices to their primes
            for (int i = 1; i <= vertices; i++) {
                if (i != s && i != t) {
                    int iPrime = i + vertices;
                    pw.format("%5d   %5d   %5d\n", i, iPrime, myInf); 
                }
            }

            pw.close();
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
       
    }
	
	public static void main(String[] args) {
		//PULL FILES FROM STORED LOCATION
		File file = new File(args[0]);
        
		//BUILD REPLICATION GRAPH FOR EACH FILE
		//MAKE UNIQUE GRAPH FILE FOR EVERY PAIR
		//IN EACH GRAPH
		CreateReplicationGraphAllPairs(file);
		
		//CONVERT EACH UNIQUE PAIR FILE INTO ITS 
		//ADJACENCY LIST(AL)
		
		//RUN ALGORITHM ON EACH AL
		int replaceWithNumberOfVertices = 5;
		MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(replaceWithNumberOfVertices);

		//ANALYZE RESULTS FOR EACH GRAPH
	}
}
