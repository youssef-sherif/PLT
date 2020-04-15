import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class DFA {

    private final Set<Character> alphabet;
    public static char EPSILON = 'Ɛ';
    public List<DFAState> DFAStates;
    private final Table<Integer, Character, Integer> DFATransitions;
    private final NFA nfa;

    public DFA(NFA nfa) {
        this.alphabet = nfa.getAlphabet();
        this.DFAStates = new ArrayList<>();
        this.DFATransitions = HashBasedTable.create();
        this.nfa = nfa;
        this.nfaToDfa();
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

    private DFAState getStateByID(Integer rowState) {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.getID().intValue() == rowState.intValue()) {
                break;
            }
            i++;
        }
        return this.DFAStates.get(i);
    }

    private DFAState getStartState() {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.isStartState()) {
                break;
            }
            i++;
        }
        return this.DFAStates.get(i);
    }

    private void nfaToDfa() {

        DFAState firstState = new DFAState(
                this.epsilonClosure(this.nfa.getStartState())
        );
        firstState.setStartState(true);

        this.DFAStates.add(firstState);

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
                // ignore dead states
                if (T.getID() == -1) continue;
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
            System.out.print("Row " + rowState + " " +
                    (getStateByID(rowState).isFinalState() ? "accept" : "") +
                    (getStateByID(rowState).isStartState() ? "start" : "") +
                    " |    ");
            for(Character col : DFATransitions.columnKeySet()){
                System.out.print(DFATransitions.get(rowState, col)+ "|     ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public boolean matches(String input) {
        ListIterator<Character> iterator = input.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList())
                .listIterator();

        Map<Integer, Map<Character, Integer>> transitionsMap = DFATransitions.rowMap();

        int currState = getStartState().getID();

        DFAState state = getStateByID(currState);
        if (state.isFinalState()) {
            return true;
        }

        try {
            while (iterator.hasNext()) {
                char char1 = iterator.next();
                currState = transitionsMap.get(currState).get(char1);
                state = getStateByID(currState);
                if (state.isFinalState()) {
                    iterator.next();
                    if (!iterator.hasNext()) {
                        return true;
                    } else if (transitionsMap.containsKey(state.getID())) {
                        currState = state.getID();
                        state = getStateByID(currState);
                        if (state.isFinalState()) {
                            if (!iterator.hasNext()) {
                                return true;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
