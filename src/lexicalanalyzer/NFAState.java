package lexicalanalyzer;

import java.util.*;

public class NFAState {

    private final int stateNo;

    private boolean finalState;
    private String ruleName;

    ArrayList<NFAState> next;
    ArrayList<Character> edges;

    public NFAState(boolean finalState,
                    int numStates) {
        this.finalState = finalState;
        this.stateNo = numStates;
        this.next = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public int getStateNo() {
        return stateNo;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public String toString() {
        return edges.toString();
    }
}