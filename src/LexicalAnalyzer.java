import lexicalanalyzer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {

    private final String rulesFileName;
    private final String programFileName;

    LexicalAnalyzer(String rulesFileName, String programFileName) {
        this.rulesFileName = rulesFileName;
        this.programFileName = programFileName;
    }

    public List<String> getTokens() throws Exception {

        LexicalRulesFile lexicalRulesFile = new LexicalRulesFile(rulesFileName);

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

        ProgramFile programFile = new ProgramFile(programFileName);

        return dfa.getTokens(programFile.getProgram());
    }
}
