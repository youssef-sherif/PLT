import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;

import java.util.List;

public class ParserAnalyzer {

    private final List<String> tokens;
    private final CFG cfg;

    public ParserAnalyzer(List<String> tokens,
                          CFGRulesFile cfgRulesFile) {
        this.tokens = tokens;
        this.cfg = new CFG(cfgRulesFile.getCFGRules());
    }

    public void parse(String program) {
        System.out.println("===CFG===");
        System.out.println(this.cfg.getProductions().toString());

        this.cfg.createFirstAndFollowSets();

        System.out.println("===FIRST===");
        System.out.println(this.cfg.getFirst());

        System.out.println("===FOLLOW===");
        System.out.println(this.cfg.getFollow());
    }
}
