import java.util.*;

public class NFA {
    //List<NFAState> graph;
    public NFAState startt;
    public NFAState finall;
    public NFA() {
    }

    public static NFA combineNFAs(List<NFA> nfaList) {
        NFA combinedNFA = new NFA();

        return combinedNFA;
    }
    public NFA edgenfa(String attr)
    {NFA nfa=new NFA();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        start.edges.add(attr);
        start.next.add(fin);
        nfa.startt=start;
        nfa.finall=fin;
        return nfa;
    }
    public NFA ORnfa(List<NFA> nfalist)
    {NFA nfa=new NFA();
        int size,i;
        size = nfalist.size();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        for(i=0;i<size;i++)
        {
            start.edges.add("epselon");
            start.next.add(nfalist.get(i).startt);
            nfalist.get(i).finall.edges.add("epselon");
            nfalist.get(i).finall.next.add(fin);
            nfalist.get(i).finall.finalstate=false;
        }
        nfa.startt=start;
        nfa.finall=fin;
        return nfa;
    }
    public NFA concnfa(List<NFA> nfalist)
    {NFA nfa=new NFA();
        int size,i;
        size = nfalist.size();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        start= nfalist.get(0).startt;

        for(i=0;i<size-1;i++)
        {nfalist.get(i).finall=nfalist.get(i+1).startt;
            nfalist.get(i).finall.finalstate=false;
        }
        nfa.startt=start;
        nfa.finall=nfalist.get(size-1).finall;

        return nfa;
    }
    public NFA astericnfa(NFA inputnfa)
    {NFA nfa=new NFA();
        NFAState start=new NFAState();
        NFAState fin=new NFAState();
        fin.finalstate=true;
        start.edges.add("epselon");
        start.next.add(inputnfa.startt);
        start.edges.add("epselon");
        start.next.add(fin);
        inputnfa.finall.edges.add("epselon");
        inputnfa.finall.next.add(inputnfa.startt);
        inputnfa.finall.edges.add("epselon");
        inputnfa.finall.next.add(fin);
        nfa.startt=start;
        nfa.finall=fin;

        return nfa;
    }
}