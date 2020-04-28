
import java.util.*;

public class DFAState {

    private String ruleName;
    private boolean finalState;
    private final List<NFAState> collectionStates;
    private final Integer id;
    private boolean marked;
    private boolean startState;

    public DFAState(List<NFAState> epsilonClosure) {
        this.startState = false;
        this.finalState = false;
        this.collectionStates = new ArrayList<>();
        this.marked = false;
        if (epsilonClosure.isEmpty()) {
//             this.id = epsilonClosure.hashCode();
            this.id = -1;
        } else {
            // TODO: I assign the DFA State id with the first id from the epsilon closure.
            // I'm not sure if this guarantees that the id is unique.
            // We can also use hashCode of it of the epsilonClosure and
            // create a HashMap that maps the hashCode to a letter.
//             this.id = epsilonClosure.hashCode();
            this.id = epsilonClosure.get(0).getStateNo();
            this.collectionStates.addAll(epsilonClosure);
        }

        for (NFAState e : this.collectionStates) {
            if (e.getRuleName() != null) {
                this.ruleName = e.getRuleName();
                if (e.isFinalState()) {
                    this.finalState = true;
                }
            }
        }
    }

    public List<NFAState> getCollectionStates() {
        return this.collectionStates;
    }

    public void mark() {
        this.marked = true;
    }

    public boolean isNotMarked() {
        return !this.marked;
    }

    public boolean isFinalState() {
        return this.finalState;
    }

    public void setStartState(boolean startState) {
        this.startState = startState;
        this.finalState = false;
    }

    public boolean isStartState() {
        return this.startState;
    }

    public Integer getID() {
        return this.id;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    @Override
    public String toString() {
        return this.getID() + " " + !this.isNotMarked() + " " + this.collectionStates;
    }
}
