/* Contains the algorithm. 
 * Needs MaxFlowMinCut.java and Cut.java to run.
 */

import java.io.*;
import java.util.*;

public class UniqueMinCutAlgorithm {

    private Set<Cut> minCuts;
    private int numberOfDistinctMinCuts;
    private int numberOfVertices;
    private String fileName;
    private String reportFileName;
    private File file;
    private PrintWriter reportpw;
    private HashMap<Integer, Integer> sSizeMap; // keeps track of the number of nodes in S in each cut

    public UniqueMinCutAlgorithm(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
        minCuts = new HashSet<Cut>();
        String[] fileNameParts = file.getAbsolutePath().split("\\.");
        reportFileName = fileNameParts[0] + "_report.txt";
        if (fileNameParts.length > 2) {
            reportFileName = "." + fileNameParts[1] + "_report.txt";
        }

        sSizeMap = new HashMap<Integer, Integer>();
    }

    public int getDistinctNumberOfMinCuts() {
        return numberOfDistinctMinCuts;
    }

    public HashMap<Integer, Integer> getSSizeMap() {
        return sSizeMap;
    }

    public void runAlgorithm() {
    	try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            // get the number of vertices in this graph from the file
            String header;
            if ((header = br.readLine()) != null) {
                header = header.trim(); // get rid of leading whitespace
                numberOfVertices = Integer.parseInt(header); 
            }
            
            int[][] adjMatrix = new int[numberOfVertices + 1][numberOfVertices + 1];
            
            // Parse the edges. Duplicate them but reverse direction
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                nextLine = nextLine.trim();
                String[] entries = nextLine.split("\\s+"); // splits into 3 numbers
                
                int firstVertex = Integer.parseInt(entries[0]);
                int secondVertex = Integer.parseInt(entries[1]);
                int edgeWeight = Integer.parseInt(entries[2]);
                
                adjMatrix[firstVertex][secondVertex] = edgeWeight;
            }
                   
            
            for (int i = 1; i <= numberOfVertices; i++) {
                for (int j = i+1; j <= numberOfVertices; j++) {
                    runFlowAlgorithm(adjMatrix, i, j);
                    runFlowAlgorithm(adjMatrix, j, i);
                }
            }
            
            //recordResults();
            br.close();
            
    	} catch (IOException e) {
            System.err.println("Error reading file");
        }
    }
    
    
    
    public void runFlowAlgorithm(int[][] adjMatrix, int source, int sink) {
    	int[][] graph;
        Cut minCut;
 
        graph = adjMatrix;
        
        /*try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));
            //System.out.println("In single pair. Finding the cut between " + s + " and " + t + ":");
            reportpw.println("Finding the cut between " + source + " and " + sink + ":");
            reportpw.close();
        } catch (IOException e) {
            System.err.println("Error");
        }*/
 
        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(numberOfVertices);
        minCut = maxFlowMinCut.maxFlowMinCut(graph, source, sink);
        
        boolean newCut = true;
        for(Cut c: minCuts) {
        	if(c.equals(minCut)) { newCut = false; }
        }
        if (newCut && minCuts.add(minCut)) {
            //reportpw.println("*** New distinct mincut found! ***");
            /*
            Integer sSize = new Integer(minCut.S.size()); // key is the size of s
            if (!sSizeMap.containsKey(sSize)) {
                sSizeMap.put(sSize, new Integer(1)); // if key doesn't exist yet, set its value to 1
            }
            else {
                sSizeMap.put(sSize, sSizeMap.get(sSize) + 1); // if key already exists, increment its value
            }
            */
        }
        
        /*try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));
            //System.out.println(minCut + "\n");    
            reportpw.println(minCut + "\n"); 
            reportpw.close();
        } catch (IOException e) {
            System.err.println("Error");
        }*/
        
    }
    
    
    
    public void recordResults() {
        //ANALYZE RESULTS FOR EACH GRAPH
        try {
            reportpw = new PrintWriter(new FileWriter(reportFileName, true));

            numberOfDistinctMinCuts = minCuts.size(); 

            /*System.out.println("****************");
            System.out.println("FINAL REPORT:");
            System.out.println("****************");

            System.out.println("The file was " + fileName + ".");
            System.out.println("The graph had " + numberOfVertices + " vertices.");
            System.out.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            System.out.println("Note: The actual number may be lower due to equivalent cuts.");
            System.out.println("Check the report file for more information.\n");*/

            /*
            reportpw.println("****************");
            reportpw.println("FINAL REPORT:");
            reportpw.println("****************");

            reportpw.println("The file was " + fileName + ".");
            reportpw.println("The graph had " + numberOfVertices + " vertices.");
            reportpw.println("The number of distinct min cuts is " + numberOfDistinctMinCuts + ".");
            reportpw.println("Note: The actual number may be lower due to equivalent cuts.\n");

            reportpw.close();*/
        }
        catch (IOException e) {
            System.err.println("ERROR");
        }

        /*//mark if it's n choose 2
        int nChooseTwo = numberOfVertices * (numberOfVertices - 1) / 2;
        if (numberOfDistinctMinCuts == nChooseTwo) {
            File file = new File(reportFileName); // old name
            String[] fileNameParts = reportFileName.split("\\.");
            String newReportFileName = fileNameParts[0] + "_GOOD.txt";
            File file2 = new File(newReportFileName); // new name
            file.renameTo(file2); // Rename file
            reportFileName = newReportFileName;
        }*/

    }

	
}
