import java.util.*;

public class NFAState {
    boolean finalstate;
    ArrayList<NFAState> next;
    ArrayList<Character> edges;

    public NFAState() {
        this.next = new ArrayList<>();
        this.edges = new ArrayList<>();
    }
}