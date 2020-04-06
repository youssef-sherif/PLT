import java.util.*;

public class NFA {
    public static char EPSILON = '@';
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
        NFA nfa = NFA.getInstance();
        nfa.alphabet.add(attr);
        NFA temp = new NFA();
        System.out.println("Character that will be added to NFA: " + attr);
        temp.startt = new NFAState(false, nfa.numStates);
        temp.finall = new NFAState(true, ++nfa.numStates);

        temp.startt.edges.add(attr);
        temp.startt.next.add(temp.finall);

        System.out.println(temp.toString());
        return temp;
    }

    public NFA or(List<NFA> nfalist) {
        NFA nfa = NFA.getInstance();
        int size,i;
        size = nfalist.size();
        NFAState start=new NFAState(false, 0);
        NFAState fin=new NFAState(true, ++NFA.getInstance().numStates);
        for(i=0;i<size;i++)
        {
            start.edges.add(EPSILON);
            start.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add(EPSILON);
            nfalist.get(i).finall.next.add(fin);
            nfalist.get(i).finall.finalState=false;
        }
        nfa.startt=start;
        nfa.finall=fin;
        return nfa;
    }

    public NFA concatenate(List<NFA> nfalist) {
        System.out.println("concatenate");
        NFA nfa = NFA.getInstance();
        int size = nfalist.size();
        if (size <= 1) {
            nfa.finall.next.add(nfalist.get(0).startt);
            nfa.finall = nfalist.get(0).finall;
            nfa.startt = nfalist.get(0).startt;
            return nfa;
        }
        nfa.startt = nfalist.get(0).startt;

        for(int i = 0; i < size-1; i++) {
            nfalist.get(i).finall.next.add(nfalist.get(i+1).startt);
            nfalist.get(i).finall.finalState = false;
        }
        nfalist.get(size-2).finall.next.add(nfalist.get(size-1).finall);
        nfalist.get(size-1).finall.finalState = true;

        nfa.finall = nfalist.get(size-1).finall;

        System.out.println(nfa.toString());
        return nfa;
    }


    public NFA asterisk(NFA inputnfa)  {
        System.out.println("asterisk");
        NFA nfa = NFA.getInstance();

        inputnfa.startt.shiftStates();

        System.out.println(inputnfa.toString());

        NFAState startState = new NFAState(false, nfa.numStates);
        NFAState finalState = new NFAState(true, ++nfa.numStates);

        startState.next.add(inputnfa.startt);
        startState.edges.add(EPSILON);

        startState.edges.add(EPSILON);
        startState.next.add(finalState);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(inputnfa.startt);

        inputnfa.finall.edges.add(EPSILON);
        inputnfa.finall.next.add(finalState);

        nfa.startt = startState;
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

        StringBuilder result = new StringBuilder("Number of states : " + Integer.toString(this.numStates+1) + "\n");
        result.append("Start state : ").append(this.startt.getStateNo()).append("\n");
        result.append("Final state : ").append(this.finall.getStateNo()).append("\n");
        result.append("States :\n");


        stack.push(this.startt.next);
//        c.add(nfa.startt.next);


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