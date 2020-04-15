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
        if (!attr.equals(EPSILON)) {
            NFA.getInstance().alphabet.add(attr);
        }
        NFA temp = new NFA();
        temp.startt = new NFAState(false, ++NFA.getInstance().numStates);
        temp.finall = new NFAState(true, ++NFA.getInstance().numStates);

        temp.startt.edges.add(attr);
        temp.startt.next.add(temp.finall);

        return temp;
    }

    public static NFA combineNFAsOr(List<NFA> nfaList) {

        int size = nfaList.size();
        NFA nfa = new NFA();
        NFAState start = new NFAState(false,  ++NFA.getInstance().numStates);
        NFAState fin = new NFAState(true, ++NFA.getInstance().numStates);

        for (int i = 0; i < size; i++) {
            start.edges.add(EPSILON);

            NFA epsilonEdge = NFA.edge(EPSILON);
            NFA epsilonEdge2 = NFA.edge(EPSILON);
            List<NFA> oRedEdge = new ArrayList<>();
            oRedEdge.add(epsilonEdge);
            oRedEdge.add(nfaList.get(i));
            oRedEdge.add(epsilonEdge2);

            start.next.add(NFA.concatenateNFAs(oRedEdge).startt);
            nfaList.get(i).finall.edges.add(EPSILON);
            nfaList.get(i).finall.next.add(fin);
        }

        nfa.startt = start;
        nfa.finall = fin;

        NFA.getInstance().startt = nfa.startt;
        NFA.getInstance().finall = nfa.finall;

        return nfa;
    }

    /*
    The difference between this function and concatenateNFAs
    is that this one updates the NFA instance and concatenateNFAs does not
    concatenateNFAs is used internally.
     */
    public static NFA combineNFAsConcatenate(List<NFA> nfaList) {

        int size = nfaList.size();
        NFA nfa = new NFA();
        NFAState start = nfaList.get(0).startt;

        for (int i = 0; i < size-1; i++) {
            nfaList.get(i).finall.edges=nfaList.get(i+1).startt.edges;
            nfaList.get(i).finall.next=nfaList.get(i+1).startt.next;
            nfaList.get(i).finall.setFinalState(false);
        }

        nfa.startt = start;
        nfa.finall = nfaList.get(size-1).finall;

        NFA.getInstance().startt = nfa.startt;
        NFA.getInstance().finall = nfa.finall;

        return nfa;
    }

    private static NFA concatenateNFAs(List<NFA> nfaList) {
        int size = nfaList.size();
        NFA nfa = new NFA();
        NFAState start = nfaList.get(0).startt;

        for (int i = 0; i < size-1; i++) {
            nfaList.get(i).finall.edges=nfaList.get(i+1).startt.edges;
            nfaList.get(i).finall.next=nfaList.get(i+1).startt.next;
            nfaList.get(i).finall.setFinalState(false);
        }

        nfa.startt = start;
        nfa.finall = nfaList.get(size-1).finall;

        return nfa;
    }

    public NFA asterisk()  {
        NFAState startState = new NFAState(false, ++NFA.getInstance().numStates);
        NFAState finalState = new NFAState(true, ++NFA.getInstance().numStates);

        startState.edges.add(EPSILON);
        startState.next.add(this.startt);

        startState.edges.add(EPSILON);
        startState.next.add(finalState);

        this.finall.edges.add(EPSILON);
        this.finall.next.add(this.startt);

        this.finall.edges.add(EPSILON);
        this.finall.next.add(finalState);

        this.startt = startState;
        this.finall = finalState;

        return this;
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