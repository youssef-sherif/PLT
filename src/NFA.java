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

    public NFA edge(Character attr) {
        NFA.getInstance().alphabet.add(attr);
        NFA temp = new NFA();
        System.out.println("Character that will be added to NFA: " + attr);
        temp.startt = new NFAState(false, ++NFA.getInstance().numStates);
        temp.finall = new NFAState(true, ++NFA.getInstance().numStates);

        temp.startt.edges.add(attr);
        temp.startt.next.add(temp.finall);

        return temp;
    }

    public NFA or(List<NFA> nfalist) {
//        System.out.print("or ");

        int size = nfalist.size();
        NFAState start = new NFAState(false,  ++NFA.getInstance().numStates);
        NFAState fin = new NFAState(true, ++NFA.getInstance().numStates);

        for (int i = 0; i < size; i++) {
            start.edges.add(EPSILON);
            start.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add(EPSILON);
            nfalist.get(i).finall.next.add(fin);
        }
//
        NFA.getInstance().startt = start;
        NFA.getInstance().finall = fin;

        return NFA.getInstance();
    }

    public NFA concatenate(List<NFA> nfalist) {
//        System.out.print("concatenate ");

        int size = nfalist.size();
        NFA.getInstance().startt = nfalist.get(0).startt;

        for(int i = 0; i < size-1; i++) {
            nfalist.get(i).finall.edges=nfalist.get(i+1).startt.edges;
            nfalist.get(i).finall.next=nfalist.get(i+1).startt.next;
            nfalist.get(i).finall.finalState = false;
        }

        NFA.getInstance().finall = nfalist.get(size-1).finall;

        return NFA.getInstance();
    }


    public NFA asterisk(NFA inputnfa)  {
//        System.out.println("asterisk");

        NFAState startState = new NFAState(false, ++nfa.numStates);
        NFAState finalState = new NFAState(true, ++nfa.numStates);

        startState.next.add(inputnfa.startt);
        startState.edges.add(EPSILON);

        startState.edges.add(EPSILON);
        startState.next.add(finalState);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(inputnfa.startt);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(finalState);

        NFA.getInstance().startt = startState;
        NFA.getInstance().finall = finalState;

        return NFA.getInstance();
    }

    public NFA plus(NFA nfa) {
//        System.out.println("plus");

        List<NFA> tempNFAs = new ArrayList<>();
        tempNFAs.add(nfa.asterisk(nfa));

        return NFA.getInstance().concatenate(tempNFAs);
    }

    public NFAState getAcceptState() {
        return NFA.getInstance().finall;
    }

    public NFAState getStartState() {
        return NFA.getInstance().startt;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String toString() {

        Stack<List<NFAState>> stack = new Stack<>();
        HashSet<List<NFAState>> c = new HashSet<>();

        StringBuilder result = new StringBuilder();
        result.append("Start state : ").append(this.startt.getStateNo()).append("\n");
        result.append("Final state : ").append(this.finall.getStateNo()).append("\n");
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