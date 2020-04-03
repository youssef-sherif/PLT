import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        RulesTokenizer rulesTokenizer = new RulesTokenizer("lexical_rules.txt");
        rulesTokenizer.tokenize();

        List<NFA> nfaList = new ArrayList<>();

        for (Map.Entry<String, String> entry : rulesTokenizer.getRegularExpressions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            RegularExpressionTokenizer regularExpressionTokenizer = new RegularExpressionTokenizer(key,
                    rulesTokenizer.getRegularDefinitions(),
                    rulesTokenizer.getKeyWords(),
                    rulesTokenizer.getPunctuation()
            );

            NFA nfa = regularExpressionTokenizer.toNFA(value);
            System.out.println("===DFA===");
            int[][] transitionTable = nfa.toTransitionTable();
            nfaList.add(nfa);
        }
        ;
    }
}
