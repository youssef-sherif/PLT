import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class DFA {

    private NFAState NFAStart;
    private DFAState DFAStart;
    private Set<Character> alphabet;
    public static char EPSILON = 'Ɛ';
    private ArrayList<DFAState> DFAStates;
    private int stateCounter = 0;
    private Table<Integer, Character, Integer> DFATransitions;
    private NFA Nfa;

    public DFA(NFA nfa) {
        this.alphabet = NFA.getInstance().getAlphabet();
        this.DFAStates = new ArrayList<>();
        this.DFATransitions = HashBasedTable.create();
        this.Nfa = nfa;
    }

    public void setDFAStart(DFAState start) {
        this.DFAStart = start;
    }

    public DFAState EpsilonClosure(DFAState state) {
        DFAState output = new DFAState(stateCounter++);
        //output.addCollectionState(state);
        //ArrayList<NFAState> output = new ArrayList<NFAState>();
        //output.add(state);
        Stack<NFAState> s = new Stack<>();
        for (NFAState nfastate : state.getCollectionStates()) {
            s.push(nfastate);
            output.addCollectionState(nfastate);
        }
        while (!s.empty()) {
            NFAState temp = (NFAState) s.pop();
            int i = 0;
            for (Character input : temp.edges) {
                if (input == EPSILON) {
                    int edgeIndex = i;
                    NFAState addToDFA = temp.next.get(edgeIndex);
                    if (!output.isCollectionState(addToDFA)) {
                        output.addCollectionState(addToDFA);
                        s.push(addToDFA);
                    }
                }
                i++;
            }
        }
        return output;
    }

    public DFAState move(DFAState state, Character input) {
        DFAState output = new DFAState(stateCounter++);
        for (NFAState nfaInDfa : state.getCollectionStates()) {
            int i = 0;
            for (Character symbol : nfaInDfa.edges) {
                if (symbol.equals(input)) {
                    int edgeIndex = i;
                    output.addCollectionState(nfaInDfa.next.get(edgeIndex));
                }
                i++;
            }
        }
        return output;
    }

    public void NFAtoDFA() {
        //DFA dfa = new DFA(nfa);
        //ArrayList<NFAState> collectionStates = dfa.EpsilonClosure(nfa.getStartState());
        DFAState startState = new DFAState(stateCounter++);
        startState.addCollectionState(this.Nfa.getStartState());
        startState = this.EpsilonClosure(startState);
        //startState.addCollectionState(collectionStates);
        this.setDFAStart(startState);
        this.DFAStates.add(startState);
        while (!this.containsMarkedState()) {
            int unmarkedIndex = this.getUnmarkedState();
                DFAState T = this.DFAStates.get(unmarkedIndex);
                T.mark();
            for (Character inputSymbol : this.alphabet) {
                DFAState u = this.EpsilonClosure(this.move(T, inputSymbol));
                if (!this.DFAStates.contains(u)) {
                    this.DFAStates.add(u);
                    T.assignNextState(u, inputSymbol);
                    this.DFATransitions.put(T.getID(), inputSymbol, u.getID());
                }
            }
        }
    }

    public void printTable(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Table size:" +this.DFATransitions.size());
        System.out.println("DFA Transition table:");
        System.out.print("           ");
        for(Character col : this.DFATransitions.columnKeySet()){
            System.out.print(col + "|     ");
        }
        System.out.println("");
        for(Integer rowState : this.DFATransitions.rowKeySet()){
            System.out.print("Row " + rowState + " |    ");
            for(Character col : this.DFATransitions.columnKeySet()){
                System.out.print(this.DFATransitions.get(rowState, col)+ "|     ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    public boolean containsMarkedState() {
        boolean flag = false;
        for (DFAState state : this.DFAStates) {
            if (state.isMarked()) {
                flag = true;
            }
        }
        return flag;
    }

    public int getUnmarkedState() {
        //DFAState temp;
        int i = 0;
        for (DFAState temp : this.DFAStates) {
            if (!temp.isMarked()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean matches(String input) {

        List<Character> inputStream = input.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());
        ListIterator<Character> iterator = inputStream.listIterator();

        Map<Integer, Map<Character, Integer>> transitionsMap = DFATransitions.rowMap();
        int currState = 1;

        while (iterator.hasNext()) {
            char char1 = iterator.next();
            System.out.println(char1);
            if (char1 == ' ') continue;
            if (!transitionsMap.containsKey(currState)) return false;
            if (!transitionsMap.get(currState).containsKey(char1))  return false;
            if (transitionsMap.get(currState).get(char1) >= DFAStates.size()) return false;
            int stateNo = transitionsMap.get(currState).get(char1);
            if (DFAStates.contains(DFAStates.get(stateNo))) {
                DFAState state = DFAStates.get(stateNo);
                if (state.isAcceptState() && !iterator.hasNext()) {
                    return true;
                } else {
                    if (transitionsMap.containsKey(state.getID())) {
                        currState = state.getID();
                    }
                }
            }
        }
        return false;
    }
}
