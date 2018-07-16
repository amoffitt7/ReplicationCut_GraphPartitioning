import java.io.*;
import java.util.*;

public class InfoCollector {

    private File folderEntry; // the folder containing all graph files
    private HashMap<Integer, Integer> distributionOfMinCuts; // keys: # of min cuts, values: # of instances of directed graphs
    private HashMap<Integer, HashMap<Integer,Integer>> distributionOfSSizes; 
        // keys: # of min cuts, values: maps (keys: S size, values: # of instances of cuts)

    public InfoCollector(File folderEntry) {
        this.folderEntry = folderEntry;
        distributionOfMinCuts = new HashMap<Integer, Integer>();
    }

    public void collectInfo() {
        deleteExistingFiles();
        // for each graph, run the algorithm and collect info
        for(final File fileEntry: listFilesForFolder(folderEntry)) {
            System.out.println("Argument string = " + fileEntry);
            ReplicationCutAlgorithm minCutAlgorithm = new ReplicationCutAlgorithm(fileEntry.getAbsolutePath());
            minCutAlgorithm.runAlgorithm();

            // get the distinct number of min cuts
            Integer minCutNumber = minCutAlgorithm.getDistinctNumberOfMinCuts();
            // add it to the map
			if (!distributionOfMinCuts.containsKey(minCutNumber)) {
				distributionOfMinCuts.put(minCutNumber, new Integer(1));
			}
			else {
				distributionOfMinCuts.put(minCutNumber, distributionOfMinCuts.get(minCutNumber) + 1);
            }
            
            // get the corresponding hash map of S sizes
            HashMap<Integer, Integer> sSizeMap = minCutAlgorithm.getSSizeMap();
            System.err.println(sSizeMap.toString());

            if (!distributionOfSSizes.containsKey(minCutNumber)) {
                distributionOfSSizes.put(minCutNumber, sSizeMap); // if this is the first time, just store the map as the value
            }
            else {
                // otherwise you have to add up previous values
                HashMap<Integer, Integer> newMap = new HashMap<Integer,Integer>();
            }
        }
    }


    /* A method to delete existing files
	 */
	public void deleteExistingFiles() {

		// delete any existing (old) distribution file
		String distributionFileName = folderEntry.getAbsolutePath() + "\\distribution.txt";
		File distributionFile = new File(distributionFileName);
		if (distributionFile.exists()) {
			boolean success = distributionFile.delete();
			if (success) {
				System.out.println("Deleted " + distributionFile.getName());
			}
		}

        // delete any existing report files
		for(File fileEntry: listFilesForFolder(folderEntry)) {
			String fileName = fileEntry.getName();
			String[] parts = fileName.split("_");
			String lastPart = parts[parts.length - 1];
			if (lastPart.equals("report.txt")
				|| lastPart.equals("GOOD.txt")) {
					boolean success = fileEntry.delete();
				if (success) {
					System.out.println("Deleted " + fileName);
				}
			}
		}
	}


    public static File[] listFilesForFolder(final File folder) {
		List<File> files = new ArrayList<File>();
	    for (final File fileEntry : folder.listFiles()) {
	        files.add(fileEntry);
	    }
	    
	    File filesArray[] = new File[files.size()];
	    return files.toArray(filesArray);
	}
}