package lexicalanalyzer;

import java.util.List;
import java.util.Set;

public class DFAFactory {

    public static DFA createDFAFromNFA(NFA nfa,
                                      Set<String> keywords,
                                      Set<String> punctuation) {

        DFA dfa = new DFA(
                nfa.getAlphabet(),
                keywords,
                punctuation
        );

        DFAState firstState = new DFAState(
                dfa.epsilonClosure(nfa.getStartState())
        );
        firstState.setStartState(true);

        dfa.addState(firstState);

        while (dfa.containsUnMarkedState()) {

            DFAState T = dfa.getUnmarkedState();
            T.mark();

            for (char inputSymbol : dfa.getAlphabet()) {
                List<NFAState> next = dfa.move(T, inputSymbol);
                DFAState U = new DFAState(
                        dfa.epsilonClosure(next)
                );
                if (!dfa.containsState(U)) {
                    dfa.addState(U);
                }
                // ignore dead states
                if (T.getID() == -1) continue;
                dfa.addTransition(T.getID(), inputSymbol, U.getID());
            }
        }

        return dfa;
    }
}
