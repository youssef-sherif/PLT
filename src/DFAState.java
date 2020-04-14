
import java.util.*;

public class DFAState {

    private boolean finalState;
    private List<NFAState> collectionStates;
    private Integer id;
    private boolean marked;

    public DFAState(List<NFAState> epsilonClosure) {
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
            // this.id = epsilonClosure.hashCode();
            this.id = epsilonClosure.get(0).getStateNo();
        }
        this.collectionStates.addAll(epsilonClosure);
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

    public Integer getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getID() + " " + !this.isNotMarked() + " " + this.collectionStates;
    }
}
