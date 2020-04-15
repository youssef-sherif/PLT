
import java.util.*;

public class DFAState {

    private boolean finalState;
    private List<NFAState> collectionStates;
    private Integer id;
    private boolean marked;
    private boolean startState;

    public DFAState(List<NFAState> epsilonClosure) {
        this.startState = false;
        this.collectionStates = new ArrayList<>();
        this.marked = false;
        if (epsilonClosure.isEmpty()) {
            // this.id = epsilonClosure.hashCode();
            this.id = -1;
        } else {
            // TODO: I assign the DFA State id with the first id from the epsilon closure.
            // I'm not sure if this guarantees that the id is unique.
            // We can also use hashCode of it of the epsilonClosure and
            // create a HashMap that maps the hashCode to a letter.
//             this.id = epsilonClosure.hashCode();
            this.id = epsilonClosure.get(0).getStateNo();
        }
        this.collectionStates.addAll(epsilonClosure);
        this.collectionStates.forEach(e -> {
            if (e.isFinalState()) {
                this.finalState = true;
            }
        });
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
    }

    public boolean isStartState() {
        return this.startState;
    }

    public Integer getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getID() + " " + !this.isNotMarked() + " " + this.collectionStates;
    }
}
