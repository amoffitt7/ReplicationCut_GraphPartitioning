/*
 * Main file for testing code
 * To run this, call "javac MaxFlowMinCut.java Cut.java ReplicationCutAlgorithm.java Run_Test.java"
 * and then call "java Run_Test filename.txt"
 */

public class Run_Test {

	public static void main(String[] args) {
		//PULL FILES FROM STORED LOCATION
        String fileName = args[0];
        System.out.println("Argument string = " + fileName);

        ReplicationCutAlgorithm repCut = new ReplicationCutAlgorithm(fileName);
        repCut.findDistinctNumberOfMinCuts();

	}
}





