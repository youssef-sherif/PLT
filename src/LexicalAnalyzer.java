import lexicalanalyzer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {

    private final LexicalRulesFile lexicalRulesFile;

    LexicalAnalyzer(LexicalRulesFile rulesFile) {
        this.lexicalRulesFile = rulesFile;
    }

    public List<String> tokenize(String program) throws Exception {

        List<NFA> nfaList = new ArrayList<>();

        System.out.println(lexicalRulesFile.toString());
        RegExp regExp = new RegExp(lexicalRulesFile);
        for (Map.Entry<String, String> entry : lexicalRulesFile.getRegularExpressions().entrySet()) {

            NFA currentNfa = regExp.toNFA(
                    entry.getKey(),
                    regExp.preProcess(entry.getValue())
            );
            nfaList.add(currentNfa);
        }

        NFA.combineNFAsOr(nfaList);

        DFA dfa = DFAFactory.createDFAFromNFA(
                NFA.getInstance(),
                lexicalRulesFile.getKeyWords(),
                lexicalRulesFile.getPunctuation()
        );
        dfa.printTable();

        return dfa.tokenize(program);
    }
}
