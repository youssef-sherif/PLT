import java.util.*;

public class NFA {
    private static NFA nfa;
    private NFAState startt;
    private NFAState finall;
    private Set<Character> alphabet;

    private NFA() { }

    public static NFA getInstance() {
        if (nfa == null) {
            nfa = new NFA();
            nfa.alphabet = new HashSet<>();
            return nfa;
        }
        return nfa;
    }

    public NFA edgeNfa(Character attr) {
        NFA nfa = NFA.getInstance();
        System.out.println("Character that will be added to NFA: " + attr);
        NFAState start = new NFAState();
        NFAState fin = new NFAState();
        fin.finalstate=true;
        start.edges.add(attr);
        start.next.add(fin);
        nfa.startt=start;
        nfa.finall=fin;
        nfa.alphabet.add(attr);

        return nfa;
    }

    public NFA or(List<NFA> nfalist) {
        NFA nfa = NFA.getInstance();
        int size,i;
        size = nfalist.size();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        for(i=0;i<size;i++)
        {
            start.edges.add('@');
            start.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add('@');
            nfalist.get(i).finall.next.add(fin);
            nfalist.get(i).finall.finalstate=false;
        }
        nfa.startt=start;
        nfa.finall=fin;
        return nfa;
    }

    public NFA concatenate(List<NFA> nfalist) {
        NFA nfa = NFA.getInstance();
        int size,i;
        size = nfalist.size();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        NFAState start = nfalist.get(0).startt;

        for(i=0;i<size-1;i++)
        {
            nfalist.get(i).finall=nfalist.get(i+1).startt;
            nfalist.get(i).finall.finalstate=false;
        }
        nfa.startt=start;
        nfa.finall=nfalist.get(size-1).finall;

        return nfa;
    }

    public NFA asterisk(NFA inputnfa)  {
        NFA nfa = NFA.getInstance();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        start.edges.add('@');
        start.next.add(inputnfa.startt);
        start.edges.add('@');
        start.next.add(fin);
        inputnfa.finall.edges.add('@');
        inputnfa.finall.next.add(inputnfa.startt);
        inputnfa.finall.edges.add('@');
        inputnfa.finall.next.add(fin);
        nfa.startt=start;
        nfa.finall=fin;

        return nfa;
    }

    public NFA plus(NFA nfa) {
        List<NFA> tempNFAs = new ArrayList<>();
        tempNFAs.add(nfa.asterisk(nfa));
        return nfa.concatenate(tempNFAs);
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

    public int[][] toTransitionTable() {
        System.out.println(nfa.getStartState().edges);
        System.out.println(nfa.getAlphabet());
        // clear NFA
        nfa = null;

        return new int[1][1];
    }
}