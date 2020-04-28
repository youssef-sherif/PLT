package lexicalanalyzer;

import static lexicalanalyzer.Constants.EPSILON;

import java.util.*;

public class NFA {

    private static NFA nfa;

    private NFAState startState;
    private NFAState finalState;
    private Set<Character> alphabet;

    private int numStates;

    private NFA() {
    }

    public static NFA getInstance() {
        if (nfa == null) {
            nfa = new NFA();
            nfa.numStates = 0;
            nfa.startState = new NFAState(false, nfa.numStates);
            nfa.alphabet = new HashSet<>();
        }
        return nfa;
    }

    public static NFA edge(Character attr) {
        if (!attr.equals(EPSILON)) {
            NFA.getInstance().alphabet.add(attr);
        }
        NFA temp = new NFA();
        temp.startState = new NFAState(false, ++NFA.getInstance().numStates);
        temp.finalState = new NFAState(true, ++NFA.getInstance().numStates);

        temp.startState.edges.add(attr);
        temp.startState.next.add(temp.finalState);

        return temp;
    }

    public static NFA combineNFAsOr(List<NFA> nfaList) {

        int size = nfaList.size();
        NFA nfa = new NFA();
        NFAState start = new NFAState(false,  ++NFA.getInstance().numStates);
        NFAState fin = new NFAState(false, ++NFA.getInstance().numStates);

        for (int i = 0; i < size; i++) {
            start.edges.add(EPSILON);

            NFA epsilonEdge = NFA.edge(EPSILON);
            NFA epsilonEdge2 = NFA.edge(EPSILON);
            List<NFA> oRedEdge = new ArrayList<>();
            oRedEdge.add(epsilonEdge);
            oRedEdge.add(nfaList.get(i));
            oRedEdge.add(epsilonEdge2);

            start.next.add(NFA.combineNFAsConcatenate(oRedEdge).startState);
            nfaList.get(i).finalState.edges.add(EPSILON);
            nfaList.get(i).finalState.next.add(fin);
        }

        nfa.startState = start;
        nfa.finalState = fin;

        NFA.getInstance().startState = nfa.startState;
        NFA.getInstance().finalState = nfa.finalState;

        return nfa;
    }

    public static NFA combineNFAsConcatenate(List<NFA> nfaList) {

        int size = nfaList.size();
        NFA nfa = new NFA();
        NFAState start = nfaList.get(0).startState;

        for (int i = 0; i < size-1; i++) {
            nfaList.get(i).finalState.edges=nfaList.get(i+1).startState.edges;
            nfaList.get(i).finalState.next=nfaList.get(i+1).startState.next;
            nfaList.get(i).finalState.setFinalState(true);
        }

        nfa.startState = start;
        nfa.finalState = nfaList.get(size-1).finalState;

        NFA.getInstance().startState = nfa.startState;
        NFA.getInstance().finalState = nfa.finalState;

        return nfa;
    }

    public NFA asterisk()  {
        NFAState startState = new NFAState(false, ++NFA.getInstance().numStates);
        NFAState finalState = new NFAState(true, ++NFA.getInstance().numStates);

        startState.edges.add(EPSILON);
        startState.next.add(this.startState);

        startState.edges.add(EPSILON);
        startState.next.add(finalState);

        this.finalState.edges.add(EPSILON);
        this.finalState.next.add(this.startState);

        this.finalState.edges.add(EPSILON);
        this.finalState.next.add(finalState);

        this.startState = startState;
        this.finalState = finalState;

        return this;
    }

    public NFAState getFinalState() {
        return this.finalState;
    }

    public NFAState getStartState() {
        return this.startState;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String toString() {

        Stack<List<NFAState>> stack = new Stack<>();
        HashSet<List<NFAState>> c = new HashSet<>();

        StringBuilder result = new StringBuilder();
        result.append("Start state : ").append(this.getStartState().getStateNo()).append("\n");
        result.append("Final state : ").append(this.getFinalState().getStateNo()).append("\n");
        result.append("States :\n");


        stack.push(this.startState.next);
        c.add(nfa.startState.next);


        result.append(this.startState.getStateNo()).append("\n");
        for (NFAState nfaState : this.startState.next) {
            result.append(nfaState.getStateNo()).append(" ");
        }
        for (Character nfaState : this.startState.edges) {
            result.append(nfaState).append(" ");
        }
        result.append("\n");

        while (!stack.empty()) {
            List<NFAState> t = stack.pop();
            for (NFAState a : t) {
                result.append(a.getStateNo()).append("\n");
                for (NFAState nfaState : a.next) {
                    result.append(nfaState.getStateNo()).append(" ");
                }
                for (Character nfaState : a.edges) {
                    result.append(nfaState).append(" ");
                }
                result.append("\n");
                if (!c.contains(a.next)) {
                    c.add(a.next);
                    stack.push(a.next);
                }
            }
        }

        return result.toString();
    }
}