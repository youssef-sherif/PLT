
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

public class DFA {
    private NFAState NFAStart;
    private DFAState DFAStart;
    private Set<Character> alphabet;
    public static char EPSILON = '@';
    private ArrayList<DFAState> DFAStates;
    
    public DFA(){
        
    }
    public DFA(NFA nfa){
        this.alphabet = nfa.getAlphabet();
    }
    
    public void setDFAStart(DFAState start){
        this.DFAStart = start;
    }
    
    public DFAState EpsilonClosure(DFAState state){
        DFAState output = new DFAState();
        //output.addCollectionState(state);
        //ArrayList<NFAState> output = new ArrayList<NFAState>();
        //output.add(state);
        Stack s = new Stack();
        for(NFAState nfastate : state.getCollectionStates()){
            s.push(nfastate);
            output.addCollectionState(nfastate);
        }
        while(!s.empty()){
            NFAState temp = (NFAState) s.pop();
            int i=0;
            for(Character input : temp.edges){
                if(input == EPSILON){
                    int edgeIndex = i;
                    NFAState addToDFA = temp.next.get(edgeIndex);
                    if(!output.isCollectionState(addToDFA)){
                    output.addCollectionState(addToDFA);
                    s.push(addToDFA);
                    }
                }
                i++;
            }
        }
        return output;
    }
    
    public DFAState move(DFAState state, Character input){
        DFAState output = new DFAState();
        for(NFAState nfaInDfa : state.getCollectionStates()){
            int i = 0;
            for(Character symbol : nfaInDfa.edges){
                if(symbol.equals(input)){
                    int edgeIndex = i;
                    output.addCollectionState(nfaInDfa.next.get(edgeIndex));
                }
                i++;
            }
        }
        return output;
    }
    public DFA DFAtoNFA(NFA nfa){
        DFA dfa = new DFA(nfa);
        //ArrayList<NFAState> collectionStates = dfa.EpsilonClosure(nfa.getStartState());
        DFAState startState = new DFAState();
        startState.addCollectionState(nfa.getStartState());
        startState = dfa.EpsilonClosure(startState);
        //startState.addCollectionState(collectionStates);
        dfa.setDFAStart(startState);
        dfa.DFAStates.add(startState);
        while(!dfa.containsMarkedState()){
            int unmarkedIndex = dfa.getUnmarkedState();
            DFAState T = dfa.DFAStates.get(unmarkedIndex);
            T.mark();
            for (Character inputSymbol : dfa.alphabet){
                DFAState u = dfa.EpsilonClosure(dfa.move(T, inputSymbol));
                if(!dfa.DFAStates.contains(u)){
                    dfa.DFAStates.add(u);
                }
                //Stopped here
            }
        }
        return dfa;
    }
    
    public boolean containsMarkedState(){
        boolean flag = false;
        for(DFAState state: this.DFAStates){
            if(state.isMarked()){
                flag = true;
            }
        }
        return flag;
    }
    
    public int getUnmarkedState(){
        //DFAState temp;
        int i=0;
        for(DFAState temp : this.DFAStates){
            if(temp.isMarked()){
                return i;
            }
            i++;
        }
        return -1;
    }
}
