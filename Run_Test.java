/*
 * Main file for testing code
 * To run this, call "javac MaxFlowMinCut.java Cut.java ReplicationCutAlgorithm.java Run_Test.java"
 * and then call "java Run_Test filename.txt"
 */

import java.io.*;
import java.util.*;


public class Run_Test {

	public static void main(String[] args) {	
		final File mainFolder = new File(args[0]);
		if (!mainFolder.exists()) {
			System.err.println("Please pass in an existing folder.");
			return;
		}
		if (!mainFolder.isDirectory()) {
			System.err.println("Please pass in a valid folder.");
			return;
		}
		deleteExistingDistributionFiles(mainFolder);
		for(final File folderEntry: mainFolder.listFiles()) {
			if(!folderEntry.isDirectory()) {
				continue;
			}
			deleteExistingFiles(folderEntry);
			for(final File fileEntry: listFilesForFolder(folderEntry)) {
				System.out.println("Argument string = " + fileEntry);
				ReplicationCutAlgorithm repCut = new ReplicationCutAlgorithm(fileEntry.getAbsolutePath());
		        repCut.findDistinctNumberOfMinCuts();
			}
			createDistributionFile(mainFolder,folderEntry);
		}

	}	

	/* A method to delete existing files
	 */
	public static void deleteExistingDistributionFiles(File mainFolder) {
		// delete any existing distribution file
		for (File fileEntry: mainFolder.listFiles()) {
			String fileName = fileEntry.getName();
			String[] parts = fileName.split("_");
			String firstPart = parts[0];
			if (firstPart.equals("distribution")) {
					boolean success = fileEntry.delete();
				if (success) {
					System.out.println("Deleted " + fileName);
				}
			}
		}
	}

	/* A method to delete existing files
	 */
	public static void deleteExistingFiles(File folderEntry) {
		File tempfile = new File("tempfile");
		if (tempfile.exists()) {
			boolean success = tempfile.delete();
			if (success) {
				System.out.println("Deleted tempfile");
			}
		}

		// delete any existing (old) distribution file
		String distributionFileName = folderEntry.getAbsolutePath() + "\\distribution.txt";
		File distributionFile = new File(distributionFileName);
		if (distributionFile.exists()) {
			boolean success = distributionFile.delete();
			if (success) {
				System.out.println("Deleted " + distributionFile.getName());
			}
		}

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

	/* A method to print the distribution among distinct number of min cuts to file.
	 */
	public static void createDistributionFile(File mainFolder, File folderEntry) {
		String[] folderNameParts = folderEntry.getName().split("_");
		int numberOfVertices = Integer.parseInt(folderNameParts[1]);
		int nChooseTwo = numberOfVertices * (numberOfVertices - 1) / 2;

		String distributionFileName = mainFolder.getAbsolutePath() + "\\distribution_" + folderEntry.getName() + ".txt";
		File distributionFile = new File(distributionFileName);
		File tempfile = new File("tempfile");

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(tempfile));
			String nextLine;
            while ((nextLine = br.readLine()) != null) {
				Integer minCutNumber = Integer.parseInt(nextLine);
				if (!map.containsKey(minCutNumber)) {
					map.put(minCutNumber, new Integer(1));
				}
				else {
					map.put(minCutNumber, map.get(minCutNumber) + 1);
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error");
		}

		boolean success = tempfile.delete();
		if (!success) {
			System.out.println("FAILURE");
		}

		try {
			PrintWriter dpw = new PrintWriter(new FileWriter(distributionFile));

			dpw.format("MinCut number | Number of graphs\r\n");
			for (int minCutNumber = numberOfVertices - 1; minCutNumber <= nChooseTwo; minCutNumber++) {
				int value = 0;
				if (map.containsKey(minCutNumber)) {
					value = map.get(new Integer(minCutNumber));
				}
				dpw.format("%13d | %15d\r\n", minCutNumber, value);
			}

			dpw.close();
		} catch (IOException e) {
			System.err.println("Error");
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
