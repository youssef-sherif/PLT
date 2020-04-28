import lexicalanalyzer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {

    private final LexicalRulesFile lexicalRulesFile;
    private final ProgramFile programFile;

    LexicalAnalyzer(LexicalRulesFile rulesFile,
                    ProgramFile programFile) {
        this.lexicalRulesFile = rulesFile;
        this.programFile = programFile;
    }

    public List<String> getTokens() throws Exception {

        List<NFA> nfaList = new ArrayList<>();

        System.out.println(lexicalRulesFile.toString());

        for (Map.Entry<String, String> entry : lexicalRulesFile.getRegularExpressions().entrySet()) {

            RegularExpression regularExpression = new RegularExpression(
                    entry.getKey(),
                    entry.getValue(),
                    lexicalRulesFile.getRegularDefinitions()
            );

            NFA currentNfa = regularExpression.toNFA();
            nfaList.add(currentNfa);
        }

        NFA.combineNFAsOr(nfaList);

        DFA dfa = new DFA(
                NFA.getInstance(),
                lexicalRulesFile.getKeyWords(),
                lexicalRulesFile.getPunctuation()
        );

        dfa.printTable();

        return dfa.getTokens(programFile.getProgram());
    }
}
