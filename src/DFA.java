import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class DFA {

    private Set<Character> alphabet;
    public static char EPSILON = 'Ɛ';
    public List<DFAState> DFAStates;
    private Table<Integer, Character, Integer> DFATransitions;
    private NFA nfa;

    public DFA(NFA nfa) {
        this.alphabet = nfa.getAlphabet();
        this.DFAStates = new ArrayList<>();
        this.DFATransitions = HashBasedTable.create();
        this.nfa = nfa;
    }

    private List<NFAState> epsilonClosure(NFAState startState) {
        return this.epsilonClosure(startState.next);
    }

    private List<NFAState> epsilonClosure(List<NFAState> states) {

        List<NFAState> closure = new ArrayList<>();

        Stack<List<NFAState>> s = new Stack<>();
        Set<List<NFAState>> visited = new HashSet<>();

        s.push(states);
        visited.add(states);

        while (!s.empty()) {
            for (NFAState poppedState : s.pop()) {
                int i = 0;
                for (Character symbol : poppedState.edges) {
                    if (symbol.equals(EPSILON)) {
                        closure.add(poppedState.next.get(i));
                    }
                    i++;
                }
                /*
                This is important because if the next does not contain any EPSILONs
                it skips it entirely and produces incorrect table.
                So we need to check if all edges in next contain EPSILON before pushing it to stack.
                If it does not contain EPSILON we add it to closure.
                 */
                if (!visited.contains(poppedState.next)) {
                    for (NFAState state : poppedState.next) {
                        if (state.edges.contains(EPSILON)) {
                            s.push(poppedState.next);
                            visited.add(poppedState.next);
                        } else {
                            closure.add(poppedState);
                        }
                    }
                }
            }
        }
        return closure;
    }

    private List<NFAState> move(DFAState state, Character input) {
        List<NFAState> next = new ArrayList<>();

        for (NFAState nfaInDfa : state.getCollectionStates()) {
            int i = 0;
            for (Character edgeSymbol : nfaInDfa.edges) {
                if (edgeSymbol.equals(input)) {
                    next.add(nfaInDfa.next.get(i));
                }
            }
        }

        return next;
    }

    private boolean containsState(DFAState next) {
        for (DFAState state : this.DFAStates) {
            if (state.getID().intValue() == next.getID().intValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsUnMarkedState() {
        for (DFAState state : this.DFAStates) {
            if (state.isNotMarked()) {
                return true;
            }
        }
        return false;
    }

    private DFAState getUnmarkedState() {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.isNotMarked()) {
                break;
            }
            i++;
        }

        return this.DFAStates.get(i);
    }

    public void nfaToDfa() {

        this.DFAStates.add(
                new DFAState(this.epsilonClosure(this.nfa.getStartState()))
        );

        while (this.containsUnMarkedState()) {

            DFAState T = this.getUnmarkedState();
            T.mark();

            for (char inputSymbol : this.alphabet) {
                List<NFAState> next = this.move(T, inputSymbol);
                DFAState U = new DFAState(
                        this.epsilonClosure(next)
                );
                if (!this.containsState(U)) {
                    this.DFAStates.add(U);
                }
                this.DFATransitions.put(T.getID(), inputSymbol, U.getID());
            }
        }
    }

    public void printTable() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Table size:" + DFATransitions.size());
        System.out.println("DFA Transition table:");
        System.out.print("           ");
        for(Character col : DFATransitions.columnKeySet()){
            System.out.print(col + "|     ");
        }
        System.out.println("");
        for(Integer rowState : DFATransitions.rowKeySet()){
            System.out.print("Row " + rowState + " |    ");
            for(Character col : DFATransitions.columnKeySet()){
                System.out.print(DFATransitions.get(rowState, col)+ "|     ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public boolean matches(String input) {

        return false;
    }
}
