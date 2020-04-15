import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try{
//            LexicalRulesFile lexicalRulesFile = new LexicalRulesFile("test_rules_1.txt");
//            LexicalRulesFile lexicalRulesFile = new LexicalRulesFile("test_rules_2.txt");
//            LexicalRulesFile lexicalRulesFile = new LexicalRulesFile("test_rules_3.txt");
            LexicalRulesFile lexicalRulesFile = new LexicalRulesFile("lexical_rules.txt");

            List<NFA> nfaList = new ArrayList<>();

            for (Map.Entry<String, String> entry : lexicalRulesFile.getRegularExpressions().entrySet()) {

                RegularExpression regularExpression = new RegularExpression(
                        entry.getKey(),
                        entry.getValue(),
                        lexicalRulesFile.getRegularDefinitions(),
                        lexicalRulesFile.getKeyWords(),
                        lexicalRulesFile.getPunctuation()
                );

                NFA currentNfa = regularExpression.toNFA();
//                System.out.println("===" + entry.getKey() + " NFA===");
//                System.out.println(currentNfa.toString());
                nfaList.add(currentNfa);
            }

            NFA.combineNFAsOr(nfaList);

            System.out.println("===NFA===");
//            System.out.println(NFA.getInstance().toString());

            System.out.println("===DFA===");
            DFA dfa = new DFA(NFA.getInstance(),
                            lexicalRulesFile.getKeyWords(),
                            lexicalRulesFile.getPunctuation()
            );
            dfa.printTable();

            ProgramFile programFile = new ProgramFile("input.txt");
            List<String> tokens = dfa.getTokens(programFile.getProgram());
            System.out.println(tokens);
        } catch (Exception e) {
            System.out.println("Syntax Error");
        }
    }
}
