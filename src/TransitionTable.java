import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransitionTable {
    protected int[][] table;
    public TransitionTable(NFA nfa) {
        NFAState state = nfa.getStartState();
        NFAState acceptState = nfa.getAcceptState();
        Set<Character> alphabet = nfa.getAlphabet();

        System.out.println(nfa.getStartState().edges);
        System.out.println(Arrays.toString(alphabet.toArray()));
    }
}
