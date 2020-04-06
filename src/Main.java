import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
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

            System.out.println("===NFA===");
            NFA nfa = regularExpressionTokenizer.toNFA(value);
//            nfa = nfa.or(nfaList);
            System.out.println(nfa.toString());
            nfaList.add(nfa);
        }
        ;
    }
}
