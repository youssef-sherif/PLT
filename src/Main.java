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

                RegularExpression regularExpressionTokenizer = new RegularExpression(
                        entry.getKey(),
                        rulesTokenizer.getRegularDefinitions(),
                        rulesTokenizer.getKeyWords(),
                        rulesTokenizer.getPunctuation()
                );

                NFA currentNfa = regularExpressionTokenizer.toNFA(entry.getValue());
                nfaList.add(currentNfa);
            }

//            NFA nfa = NFA.getInstance().or(nfaList);

            System.out.println("===NFA===");
            System.out.println(NFA.getInstance().toString());


            System.out.println("===DFA===");
            DFA dfa = new DFA(NFA.getInstance());
            dfa.NFAtoDFA();
            dfa.printTable();

            ProgramFile programFile = new ProgramFile("test.txt");
            boolean match = dfa.matches(programFile.getProgram());
            if (match) {
                System.out.println("match");
            } else {
                System.out.println("no match");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
