import java.util.*;

public class NFA {
    public static char EPSILON = '@';
    private static NFA nfa;
    private NFAState startt;
    private NFAState finall;
    private Set<Character> alphabet;
    private int numStates;

    private NFA() { }

    public static NFA getInstance() {
        if (nfa == null) {
            nfa = new NFA();
            nfa.startt = new NFAState(false, nfa.numStates);
            nfa.alphabet = new HashSet<>();
            nfa.numStates = 0;
        }
        return nfa;
    }

    public NFA edgeNfa(Character attr) {
        nfa.numStates++;
        NFA nfa = NFA.getInstance();
        System.out.println("Character that will be added to NFA: " + attr);
        NFAState fin = new NFAState(true, nfa.numStates);
        fin.finalState =true;
        nfa.startt.edges.add(attr);
        nfa.startt.next.add(fin);
        nfa.finall = fin;
        nfa.alphabet.add(attr);

        return nfa;
    }

    public NFA or(List<NFA> nfalist) {
        NFA nfa = NFA.getInstance();
        int size,i;
        size = nfalist.size();
        NFAState fin = new NFAState(true, nfa.numStates);
        for(i=0;i<size;i++) {
            nfa.startt.edges.add(EPSILON);
            nfa.startt.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add(EPSILON);
            nfalist.get(i).finall.next.add(fin);
            nfalist.get(i).finall.finalState =false;
        }
        nfa.finall = fin;

        return nfa;
    }

    public NFA concatenate(List<NFA> nfalist) {
        NFA nfa = NFA.getInstance();
        int size,i;
        size = nfalist.size();
        NFAState fin = new NFAState(true, nfa.numStates++);
        fin.finalState =true;
        nfa.startt = nfalist.get(0).startt;

        for(i=0;i<size-1;i++) {
            nfalist.get(i).finall = nfalist.get(i+1).startt;
            nfalist.get(i).finall.finalState = false;
        }
        nfa.finall=nfalist.get(size-1).finall;

        return nfa;
    }

    public NFA asterisk(NFA inputnfa)  {
        NFA nfa = NFA.getInstance();
        NFAState finalState=new NFAState(true, nfa.numStates);
        nfa.startt.edges.add(EPSILON);
        nfa.startt.next.add(inputnfa.startt);
        nfa.startt.edges.add(EPSILON);
        nfa.startt.next.add(finalState);
        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(inputnfa.startt);
        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(finalState);
        nfa.finall=finalState;

        return nfa;
    }

    public NFA plus(NFA nfa) {
        List<NFA> tempNFAs = new ArrayList<>();
        tempNFAs.add(nfa.asterisk(nfa));
        return nfa.concatenate(tempNFAs);
    }

    public NFAState getAcceptState() {
        return nfa.finall;
    }

    public NFAState getStartState() {
        return nfa.startt;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String toString() {

        Stack<List<NFAState>> stack = new Stack<>();
        HashSet<List<NFAState>> c = new HashSet<>();

        StringBuilder result = new StringBuilder("Number of states : " + Integer.toString(nfa.numStates+1) + "\n");
        result.append("Start state : ").append(nfa.startt.getStateNo()).append("\n");
        result.append("Final state : ").append(nfa.finall.getStateNo()).append("\n");
        result.append("States :\n");

        stack.push(nfa.startt.next);
        c.add(nfa.startt.next);


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