import java.util.*;

public class NFA {
    public static char EPSILON = 'Ɛ';
    private static NFA nfa;
    private NFAState startt;
    private NFAState finall;
    private Set<Character> alphabet;
    private int numStates;

    private NFA() {
    }

    public static NFA getInstance() {
        if (nfa == null) {
            nfa = new NFA();
            nfa.numStates = 0;
            nfa.startt = new NFAState(false, nfa.numStates);
            nfa.alphabet = new HashSet<>();
        }
        return nfa;
    }

    public static NFA edge(Character attr) {
        NFA.getInstance().alphabet.add(attr);
        NFA temp = new NFA();
        System.out.println("Character that will be added to NFA: " + attr);
        temp.startt = new NFAState(false, ++NFA.getInstance().numStates);
        temp.finall = new NFAState(true, ++NFA.getInstance().numStates);

        temp.startt.edges.add(attr);
        temp.startt.next.add(temp.finall);

        return temp;
    }

    public static NFA combineNFAsOr(List<NFA> nfalist) {

        int size = nfalist.size();
        NFA nfa = new NFA();
        NFAState start = new NFAState(false,  ++NFA.getInstance().numStates);
        NFAState fin = new NFAState(true, ++NFA.getInstance().numStates);

        for (int i = 0; i < size; i++) {
            start.edges.add(EPSILON);
            start.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add(EPSILON);
            nfalist.get(i).finall.next.add(fin);
        }

        nfa.startt = start;
        nfa.finall = fin;

        return nfa;
    }

    public static NFA combineNFAsConcatenate(List<NFA> nfalist) {

        int size = nfalist.size();
        NFA nfa = new NFA();
        NFAState start = nfalist.get(0).startt;

        for(int i = 0; i < size-1; i++) {
            nfalist.get(i).finall.edges=nfalist.get(i+1).startt.edges;
            nfalist.get(i).finall.next=nfalist.get(i+1).startt.next;
            nfalist.get(i).finall.finalState = false;
        }

        nfa.startt = start;
        nfa.finall = nfalist.get(size-1).finall;

        return nfa;
    }

    public static NFA asterisk(NFA inputnfa)  {
        NFAState startState = new NFAState(false, ++NFA.getInstance().numStates);
        NFAState finalState = new NFAState(true, ++NFA.getInstance().numStates);

        startState.next.add(inputnfa.startt);
        startState.edges.add(EPSILON);

        startState.edges.add(EPSILON);
        startState.next.add(finalState);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(inputnfa.startt);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(finalState);

        inputnfa.startt = startState;
        inputnfa.finall = finalState;

        return inputnfa;
    }

    public static NFA plus(NFA nfa) {
        List<NFA> tempNFAs = new ArrayList<>();
        tempNFAs.add(nfa);
        tempNFAs.add(NFA.asterisk(nfa));

        return NFA.combineNFAsConcatenate(tempNFAs);
    }

    public NFAState getAcceptState() {
        return this.finall;
    }

    public NFAState getStartState() {
        return this.startt;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String toString() {

        Stack<List<NFAState>> stack = new Stack<>();
        HashSet<List<NFAState>> c = new HashSet<>();

        StringBuilder result = new StringBuilder();
        result.append("Start state : ").append(this.getStartState().getStateNo()).append("\n");
        result.append("Final state : ").append(this.getAcceptState().getStateNo()).append("\n");
        result.append("States :\n");


        stack.push(this.startt.next);
        c.add(nfa.startt.next);


        result.append(this.startt.getStateNo()).append("\n");
        for (NFAState nfaState : this.startt.next) {
            result.append(nfaState.getStateNo()).append(" ");
        }
        for (Character nfaState : this.startt.edges) {
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