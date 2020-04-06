import java.util.*;

public class NFAState {
    private int numStates;
    boolean finalState;
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

    public String toString() {
        return edges.toString();
    }
}