/* Encapsulates information about the cut, including the weight of the cut and the set partitions S, T, and R
 */

import java.util.Collections;
import java.util.ArrayList;

public class Cut {
    public int maxFlow;
    public ArrayList<Integer> S;
    public ArrayList<Integer> T;
    public ArrayList<Integer> pathLengths;

    public Cut(int maxFlow, ArrayList<Integer> S, ArrayList<Integer> T, ArrayList<Integer> pathLengths) {
        this.maxFlow = maxFlow;
        this.S = S;
        this.T = T;
        this.pathLengths = pathLengths;

        sortSets();
    }

    private void sortSets() {
        Collections.sort(S);
        Collections.sort(T);
    }

    
    public String toString() {
        String current = "S: {";
        for (int i = 0; i < S.size(); i++) {
            if (i != S.size() - 1) {
                current = current + S.get(i) + ", ";
            }
            else {
                current = current + S.get(i);
            }
        }
        current = current + "} T: {";
        for (int i = 0; i < T.size(); i++) {
            if (i != T.size() - 1) {
                current = current + T.get(i) + ", ";
            }
            else {
                current = current + T.get(i);
            }
        }
        current = current + "} \n";
        current = current + "Cut weight is ";
        current = current + maxFlow;
        return current;
    }

    public boolean equals(Object obj) { 
        if (obj != null && getClass() == obj.getClass()) {
            Cut other = (Cut) obj;

            if (this.S.equals(other.S) && this.T.equals(other.T) && (this.maxFlow == other.maxFlow)) {
                return true;
            }
            else if (this.S.equals(other.T) && this.T.equals(other.S) && (this.maxFlow == other.maxFlow)) {
                return true;
            }
            else {
                return false;
            }
        } 

        return false;
        
    }
}
