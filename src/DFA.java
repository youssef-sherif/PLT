import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class DFA {

    private NFAState NFAStart;
    private DFAState DFAStart;
    private Set<Character> alphabet;
    public static char EPSILON = '@';
    private ArrayList<DFAState> DFAStates;
    private int stateCounter = 0;
    private Table<Integer, Character, Integer> DFATransitions;

    public DFA(NFA nfa) {
        this.alphabet = nfa.getAlphabet();
        this.DFAStates = new ArrayList<DFAState>();
        this.DFATransitions = HashBasedTable.create();
    }

    public void setDFAStart(DFAState start) {
        this.DFAStart = start;
    }

    public DFAState EpsilonClosure(DFAState state) {
        DFAState output = new DFAState(stateCounter++);
        //output.addCollectionState(state);
        //ArrayList<NFAState> output = new ArrayList<NFAState>();
        //output.add(state);
        Stack s = new Stack();
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

    public DFA DFAtoNFA(NFA nfa) {
        DFA dfa = new DFA(nfa);
        //ArrayList<NFAState> collectionStates = dfa.EpsilonClosure(nfa.getStartState());
        DFAState startState = new DFAState(stateCounter++);
        startState.addCollectionState(nfa.getStartState());
        startState = dfa.EpsilonClosure(startState);
        //startState.addCollectionState(collectionStates);
        dfa.setDFAStart(startState);
        dfa.DFAStates.add(startState);
        while (!dfa.containsMarkedState()) {
            int unmarkedIndex = dfa.getUnmarkedState();
            DFAState T = dfa.DFAStates.get(unmarkedIndex);
            T.mark();
            for (Character inputSymbol : dfa.alphabet) {
                DFAState u = dfa.EpsilonClosure(dfa.move(T, inputSymbol));
                if (!dfa.DFAStates.contains(u)) {
                    dfa.DFAStates.add(u);
                    T.assignNextState(u, inputSymbol);
                    dfa.DFATransitions.put(T.getID(), inputSymbol, u.getID());
                }
            }
        }
        return dfa;
    }

    public void printTable(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("DFA Transition table:");
        System.out.print("  ");
        for(Character col : this.DFATransitions.columnKeySet()){
            System.out.print(col + " |");
        }
        for(Cell<Integer, Character, Integer> cell : this.DFATransitions.cellSet()){
            System.out.println(cell.getRowKey() + " |");
            for(Cell<Integer, Character, Integer> cellValue : this.DFATransitions.cellSet()){
                System.out.println(cell.getColumnKey() + " |");
            }
        }
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
            if (temp.isMarked()) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
