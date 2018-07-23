/*
 * Main file for testing code
 * To run this, call "javac MaxFlowMinCut.java Cut.java ReplicationCutAlgorithm.java InfoCollector.java Run_Test.java"
 * and then call "java Run_Test folderName"
 */

import java.io.*;
import java.util.*;


public class Run_Test {

	public static void main(String[] args) {	
		final File mainFolder = new File(args[0]);

		// check if main folder passed in is valid
		if (!mainFolder.exists()) {
			System.err.println("Please pass in an existing folder.");
			return;
		}
		if (!mainFolder.isDirectory()) {
			System.err.println("Please pass in a valid folder.");
			return;
		}

		// delete existing distribution files in the main folder
		deleteExistingDistributionFiles(mainFolder);

		// iterate through each folder in main folder
		for(final File folderEntry: mainFolder.listFiles()) {

			// if it's not a folder continue
			if(!folderEntry.isDirectory()) {
				continue;
			}
			
			// create an info collector object for it
			InfoCollector folderCollector = new InfoCollector(folderEntry);
			folderCollector.collectInfo();
			File distributionFile = folderCollector.getDistributionFile();
			File distributionSFile = folderCollector.getSSizeDistributionFile();
			File distributionRFile = folderCollector.getRSizeDistributionFile();
			File newSFile = folderCollector.getNewSFile();
			File newRFile = folderCollector.getNewRFile();
			moveDistributionFile(mainFolder, distributionFile);
			moveDistributionFile(mainFolder, distributionSFile);
			moveDistributionFile(mainFolder, distributionRFile);
			moveDistributionFile(mainFolder, newSFile);
			moveDistributionFile(mainFolder, newRFile);
		}

	}	

	/* A method to delete existing files */
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
	

	/* A method to move the distribution file to the outer folder.
	 */
	public static void moveDistributionFile(File mainFolder, File distributionFile) {
		if (distributionFile.exists()) {
			// move it to outer folder
			File newLocation = new File(mainFolder.getAbsolutePath() + "/" + distributionFile.getName());
			distributionFile.renameTo(newLocation);
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
