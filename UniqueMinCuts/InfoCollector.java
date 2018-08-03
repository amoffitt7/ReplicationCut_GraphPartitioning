/* Needs UniqueMinCutAlgorithm.java to run
 * FOR UNIQUE MIN CUTS
 */

import java.io.*;
import java.util.*;

public class InfoCollector {

    private int numberOfVertices; // the number of vertices for this folder of graphs
    private int nPermTwo; 
    private File mainFolder; // the outer folder (kept here for naming purposes)
    private File folderEntry; // the folder containing all graph files
    private HashMap<Integer, Integer> distributionOfMinCuts; // keys: # of min cuts, values: # of instances of directed graphs
    private HashMap<Integer, HashMap<Integer,Integer>> distributionOfSSizes; 
        // keys: # of min cuts, values: maps (keys: S size, values: # of instances of cuts)
    private HashMap<Integer, HashMap<File, HashMap<Integer,Integer>>> newSSizes;
    //private File newSFile; // new S file (7/20/18) that separates data by graphs

    public InfoCollector(File mainFolder, File folderEntry) {
        this.mainFolder = mainFolder;
        this.folderEntry = folderEntry;
        distributionOfMinCuts = new HashMap<Integer, Integer>();
        //distributionOfSSizes = new HashMap<Integer, HashMap<Integer,Integer>>();

        String[] folderNameParts = folderEntry.getName().split("_");
		numberOfVertices = Integer.parseInt(folderNameParts[1]);
        nPermTwo = numberOfVertices * (numberOfVertices - 1);
        //newSSizes = new HashMap<Integer, HashMap<File, HashMap<Integer, Integer>>>();
    }

    public void collectInfo() {
        deleteExistingFiles();
        // for each graph, run the algorithm and collect info
        for(final File fileEntry: listFilesForFolder(folderEntry)) {
            System.out.println("Argument string = " + fileEntry);
            UniqueMinCutAlgorithm minCutAlgorithm = new UniqueMinCutAlgorithm(fileEntry.getAbsolutePath());
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
            
            /*
            // get the corresponding hash map of S sizes
            HashMap<Integer, Integer> sSizeMap = minCutAlgorithm.getSSizeMap();

            if (!distributionOfSSizes.containsKey(minCutNumber)) { // if this is the first time, just store the map as the value
                distributionOfSSizes.put(minCutNumber, sSizeMap); 
            }
            else { // otherwise you have to add up previous values
                // from https://stackoverflow.com/questions/4299728/how-can-i-combine-two-hashmap-objects-containing-the-same-types 
                HashMap<Integer, Integer> newMap = new HashMap<Integer,Integer>(distributionOfSSizes.get(minCutNumber)); 
                    // new map copies old values
                // from https://stackoverflow.com/questions/39513941/use-plus-operator-of-integer-as-bifunction 
                // the Java 8 version:
                //sSizeMap.forEach((k, v) -> newMap.merge(k, v, Integer::sum)); // add new values. If duplicates, add them together
                // the Java 7 version:
                for (Integer sSize : sSizeMap.keySet()) {
                    if (newMap.containsKey(sSize)) {
                        newMap.put(sSize, newMap.get(sSize) + sSizeMap.get(sSize));
                    }
                    else {
                        newMap.put(sSize, sSizeMap.get(sSize));
                    }
                }
                distributionOfSSizes.put(minCutNumber, newMap);
            }

            if (!newSSizes.containsKey(minCutNumber)) {
                HashMap<File, HashMap<Integer,Integer>> listToPut = new HashMap<File, HashMap<Integer,Integer>>();
                listToPut.put(fileEntry, sSizeMap);
                newSSizes.put(minCutNumber, listToPut);
            }
            else {
                HashMap<File, HashMap<Integer,Integer>> listToPut = newSSizes.get(minCutNumber);
                listToPut.put(fileEntry, sSizeMap);
                newSSizes.put(minCutNumber, listToPut);
            }
            //writeToSFile(fileEntry, minCutNumber, sSizeMap);
            */
        }
    }

    public File getNewSFile() {
        File newSFile = new File(mainFolder.getAbsolutePath() + "/new_s_" + folderEntry.getName() + ".txt");
        try {
            PrintWriter spw = new PrintWriter(new FileWriter(newSFile, true)); // appends to S file

            for (int minCutNumber = numberOfVertices - 1; minCutNumber <= nPermTwo; minCutNumber++) {
                if (newSSizes.containsKey(minCutNumber)) {
                    for (File fileEntry : newSSizes.get(minCutNumber).keySet()) {
                        HashMap<Integer,Integer> sSizeMap = newSSizes.get(minCutNumber).get(fileEntry);
                        String graphFileName = fileEntry.getName();
                        spw.format("%s: \r\n", graphFileName);
                        spw.format("Min Cuts: %d \r\n", minCutNumber);
                        spw.format("S Size | Number of Cuts\r\n");
                        for (int sSize = 1; sSize <= numberOfVertices - 1; sSize++) {
                            int numberOfCuts = 0;
                            if (sSizeMap.containsKey(sSize)) {
                                numberOfCuts = sSizeMap.get(sSize);
                            }
                            spw.format("%6d | %13d\r\n", sSize, numberOfCuts);
                        }
                        spw.format("\r\n");
                    }
                }
            }
            
            spw.close();
        } catch (IOException e) {
			System.err.println("Error");
        }

        return newSFile;
    }

    // creates and returns a distribution file
    public File getDistributionFile() {
		String distributionFileName = mainFolder.getAbsolutePath() + "/distribution_flowcut_" + folderEntry.getName() + ".txt";
		File distributionFile = new File(distributionFileName);
		try {
			PrintWriter dpw = new PrintWriter(new FileWriter(distributionFile));

			dpw.format("MinCut number | Number of graphs\r\n");
			for (int minCutNumber = numberOfVertices - 1; minCutNumber <= nPermTwo; minCutNumber++) {
				int value = 0;
				if (distributionOfMinCuts.containsKey(minCutNumber)) {
					value = distributionOfMinCuts.get(new Integer(minCutNumber));
				}
				dpw.format("%13d | %15d\r\n", minCutNumber, value);
			}

			dpw.close();
		} catch (IOException e) {
			System.err.println("Error");
        }
        
        return distributionFile;
    }

    public File getSSizeDistributionFile() {
        String distributionFileName = mainFolder.getAbsolutePath() + "/distribution_flowcut_s_" + folderEntry.getName() + ".txt";
        File distributionFile = new File(distributionFileName);
        try {
			PrintWriter dpw = new PrintWriter(new FileWriter(distributionFile));

			dpw.format("MinCut number | SSize | Number of cuts\r\n");
			for (int minCutNumber = numberOfVertices - 1; minCutNumber <= nPermTwo; minCutNumber++) {
                for (int sSize = 1; sSize <= numberOfVertices - 1; sSize++) {
                    if (distributionOfSSizes.containsKey(minCutNumber)) {
                        int numberOfCuts = 0;
                        if (distributionOfSSizes.get(minCutNumber).containsKey(sSize)) {
                            numberOfCuts = distributionOfSSizes.get(minCutNumber).get(sSize);
                        }
                        dpw.format("%13d | %5d | %13d\r\n", minCutNumber, sSize, numberOfCuts);
                    }
                }
				dpw.format("----------------------------------\r\n");
			}

			dpw.close();
		} catch (IOException e) {
			System.err.println("Error");
        }
        
        return distributionFile;

    }

    /* A method to delete existing files
	 */
	public void deleteExistingFiles() {

		// delete any existing (old) distribution file
		String distributionFileName = folderEntry.getAbsolutePath() + "/distribution.txt";
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
            String secondToLastPart = parts[parts.length - 2];
			if (lastPart.equals("report.txt")
				|| secondToLastPart.equals("report")) {
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