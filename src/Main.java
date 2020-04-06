import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        GrammarRules rulesTokenizer = new GrammarRules("lexical_rules.txt");
        rulesTokenizer.tokenize();

        NFA nfa = null;
        List<NFA> nfaList = new ArrayList<>();

        for (Map.Entry<String, String> entry : rulesTokenizer.getRegularExpressions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            RegularExpression regularExpressionTokenizer = new RegularExpression(key,
                    rulesTokenizer.getRegularDefinitions(),
                    rulesTokenizer.getKeyWords(),
                    rulesTokenizer.getPunctuation()
            );

            NFA currentNfa = regularExpressionTokenizer.toNFA(value);
            nfaList.add(currentNfa);
        }

        if (nfaList.size() > 2) {
            nfa = nfa.or(nfaList);
        }
        else {
            nfa = nfaList.get(0);
        }
        System.out.println("===NFA===");
        System.out.println(nfa.toString());
//
//        System.out.println("===DFA===");
//        DFA dfa = new DFA(nfa);
//        dfa = dfa.DFAtoNFA(nfa);
//        dfa.printTable();
    }
}
