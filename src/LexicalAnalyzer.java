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
        RegExp regExp = new RegExp(lexicalRulesFile);
        NFA nfa = regExp.toNFA();
        DFA dfa = DFAFactory.createDFAFromNFA(
                nfa,
                lexicalRulesFile.getKeyWords(),
                lexicalRulesFile.getPunctuation()
        );
        dfa.printTable();

        return dfa.tokenize(program);
    }
}
