package lexicalanalyzer;

import static lexicalanalyzer.Constants.EPSILON;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class DFA {

    private final Set<Character> alphabet;
    private final Set<String> punctuation;
    private final Set<String> keyWords;
    private final List<DFAState> DFAStates;
    private final Table<Integer, Character, Integer> DFATransitions;

    public DFA(Set<Character> alphabet,
               Set<String> keyWords,
               Set<String> punctuation) {
        this.alphabet = alphabet;
        this.DFAStates = new ArrayList<>();
        this.DFATransitions = HashBasedTable.create();
        this.keyWords = keyWords;
        this.punctuation = punctuation;
    }

    public List<NFAState> epsilonClosure(NFAState startState) {
        return this.epsilonClosure(startState.next);
    }

    public List<NFAState> epsilonClosure(List<NFAState> states) {

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

    public List<NFAState> move(DFAState state, Character input) {
        List<NFAState> next = new ArrayList<>();

        for (NFAState nfaInDfa : state.getCollectionStates()) {
            int i = 0;
            for (Character edgeSymbol : nfaInDfa.edges) {
                if (edgeSymbol.equals(input)) {
                    next.add(nfaInDfa.next.get(i));
                }
                i++;
            }
        }

        return next;
    }

    public void addState(DFAState state) {
        this.DFAStates.add(state);
    }

    public void addTransition(Integer id, char inputSymbol, Integer id1) {
        this.DFATransitions.put(id, inputSymbol, id1);
    }

    public boolean containsState(DFAState next) {
        for (DFAState state : this.DFAStates) {
            if (state.getID().intValue() == next.getID().intValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsUnMarkedState() {
        for (DFAState state : this.DFAStates) {
            if (state.isNotMarked()) {
                return true;
            }
        }
        return false;
    }

    public DFAState getUnmarkedState() {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.isNotMarked()) {
                break;
            }
            i++;
        }

        return this.DFAStates.get(i);
    }

    public DFAState getStateByID(Integer rowState) {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.getID().intValue() == rowState.intValue()) {
                break;
            }
            i++;
        }
        return this.DFAStates.get(i);
    }

    public DFAState getStartState() {
        int i = 0;
        for (DFAState state : this.DFAStates) {
            if (state.isStartState()) {
                break;
            }
            i++;
        }
        return this.DFAStates.get(i);
    }

    public Set<Character> getAlphabet() {
        return this.alphabet;
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
            DFAState state = getStateByID(rowState);
            System.out.print("Row " + rowState + " " +
                    (state.isFinalState() ? "accept" +
                            " " + state.getRuleName() + " " : "") +
                    (state.isStartState() ? "start" : "") +
                    " |    ");
            for(Character col : DFATransitions.columnKeySet()){
                System.out.print(DFATransitions.get(rowState, col)+ "|     ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public String findTokenType(String input) throws Exception {
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
            return state.getRuleName();
        }

        while (iterator.hasNext()) {
            char char1 = iterator.next();
            if (!transitionsMap.containsKey(currState)
                    || !transitionsMap.get(currState).containsKey(char1)) {
                throw new Exception();
            }
            currState = transitionsMap.get(currState).get(char1);
            state = getStateByID(currState);
            if (state.isFinalState()) {
                if (!iterator.hasNext()) {
                    return state.getRuleName();
                } else if (transitionsMap.containsKey(state.getID())) {
                    currState = state.getID();
                    state = getStateByID(currState);
                    if (state.isFinalState()) {
                        if (!iterator.hasNext()) {
                            return state.getRuleName();
                        }
                    }
                } else {
                    throw new Exception();
                }
            }
        }

        throw new Exception();
    }

    public List<String> tokenize(String input) throws Exception {

        String input1 = input + " ";
        List<String> toReturn = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (Character char1 : input1.toCharArray()) {
            if (char1 != ' '
                    && (int) char1 != 9
                    && char1 != '\n') {
                stringBuilder.append(char1);
            }
            else {
                String word = stringBuilder.toString();
                if (word.isEmpty()) continue;
                if (punctuation.contains(word)) {
                    toReturn.add(word);
                }
                else {
                    if (keyWords.contains(word)) {
                        toReturn.add(word);
                    } else {
                        toReturn.add(findTokenType(word));
                    }
                }
                stringBuilder = new StringBuilder();
            }
        }
        return toReturn;
    }
}
