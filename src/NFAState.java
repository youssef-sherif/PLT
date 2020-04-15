import java.util.*;

public class NFAState {
    private int numStates;
    private boolean finalState;
    ArrayList<NFAState> next;
    ArrayList<Character> edges;

    public NFAState(boolean finalState, int numStates) {
        this.finalState = finalState;
        this.numStates = numStates;
        this.next = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public int getStateNo() {
        return numStates;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public String toString() {
        return edges.toString();
    }
}