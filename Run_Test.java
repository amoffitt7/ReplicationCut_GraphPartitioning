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
		for(final File folderEntry: mainFolder.listFiles()) {
			for(final File fileEntry: listFilesForFolder(folderEntry)) {
				System.out.println("Argument string = " + fileEntry);
				ReplicationCutAlgorithm repCut = new ReplicationCutAlgorithm(fileEntry.getAbsolutePath());
		        repCut.findDistinctNumberOfMinCuts();
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
