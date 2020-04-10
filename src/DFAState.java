
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFAState {

    private boolean finalstate;
    private Map<Character, DFAState> transitions;
    private ArrayList<NFAState> collectionStates;
    private Integer id;
    private boolean marked;
    private HashMap<DFAState, Character> TransTable;

    public DFAState(Integer id) {
        //    this.transitions = new Map<Character, DFAState>();
        this.collectionStates = new ArrayList<NFAState>();
        this.id = id;
        this.marked = false;
        this.TransTable = new HashMap<DFAState, Character>();
    }

    public void addCollectionState(NFAState inputState) {
        // for(NFAState state: inputState){
        if (inputState.finalState) {
            this.finalstate = true;
        }
        this.collectionStates.add(inputState);
        //}
    }

    public ArrayList<NFAState> getCollectionStates() {
        return this.collectionStates;
    }

    public boolean isCollectionState(NFAState inputState) {
        if (this.collectionStates.contains(inputState)) {
            return true;
        } else {
            return false;
        }
    }

    public void mark() {
        this.marked = true;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void assignNextState(DFAState nextState, Character inputSymbol) {
        try {
            this.TransTable.put(nextState, inputSymbol);
            System.out.println("Transition complete"
                    + " Next DFAState: " + nextState.getID() + " next symbol: " + inputSymbol + 
                    " for state: " + this.getID());
        } catch(Exception e)  {
            System.out.println("Tranisition failed for DFA state number: " + this.getID());
        }
    }

    public boolean isAcceptState() {
        return this.finalstate;
    }

    public Integer getID() {
        return this.id;
    }
    
}