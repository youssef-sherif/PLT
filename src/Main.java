import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try{
            GrammarRules rulesTokenizer = new GrammarRules("lexical_rules.txt");
            rulesTokenizer.tokenize();

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

            NFA nfa = NFA.getInstance().or(nfaList);

            System.out.println("===NFA===");
            System.out.println(nfa.toString());


            System.out.println("===DFA===");
            DFA dfa = new DFA(nfa);
            dfa.DFAtoNFA();
            dfa.printTable();
        } catch (Exception e) {
            System.out.println("here");
            e.printStackTrace();
        }
    }
}
