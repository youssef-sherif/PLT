import lexicalanalyzer.*;

import java.util.List;

public class LexicalAnalyzer {

    private final LexicalRulesFile lexicalRulesFile;

    LexicalAnalyzer(LexicalRulesFile rulesFile) {
        this.lexicalRulesFile = rulesFile;
    }

    public List<String> tokenize(String program) throws Exception {
        RegExp regExp = new RegExp(lexicalRulesFile);
        NFA nfa = regExp.toNFAAll();
        DFA dfa = DFAFactory.createDFAFromNFA(
                nfa,
                lexicalRulesFile.getKeyWords(),
                lexicalRulesFile.getPunctuation()
        );
        dfa.printTable();

        return dfa.tokenize(program);
    }
}
