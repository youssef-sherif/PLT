
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFAState {

    private boolean finalstate;
    private Map<Character, DFAState> transitions;
    private ArrayList<NFAState> collectionStates;
    private Integer id;
    private boolean marked = false;
    private HashMap<DFAState, Character> TransTable;

    public DFAState(Integer id) {
        //    this.transitions = new Map<Character, DFAState>();
        this.collectionStates = new ArrayList<NFAState>();
        this.id = id;
        //this.TransTable = new
    }

    public void addCollectionState(NFAState inputState) {
        // for(NFAState state: inputState){
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

    public String assignNextState(DFAState nextState, Character inputSymbol) {
        try {
            this.TransTable.put(nextState, inputSymbol);
            return ("Transition complete"
                    + " Next DFAState: " + nextState.getID() + " next symbol: " + inputSymbol + 
                    "for state: " + this.getID());
        } catch(Exception e)  {
            return ("Tranisition failed for DFA state number: " + this.getID());
        }
    }

    public Integer getID() {
        return this.id;
    }
    
}
