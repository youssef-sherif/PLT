import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;

import java.util.List;

public class ParserAnalyzer {

    private final List<String> tokens;
    private final CFG cfg;

    public ParserAnalyzer(List<String> tokens,
                          CFGRulesFile cfgRulesFile) {
        this.tokens = tokens;
        this.tokens.add("$");
        this.cfg = new CFG(cfgRulesFile.getCFGRules());
    }

    public void parse() throws Exception {
        this.cfg.createFirstAndFollowSets();
        this.cfg.createLL1Table(this.cfg.getFirst(), this.cfg.getFollow());

        System.out.println(cfg.toString());
        System.out.println(cfg.parse(tokens));
    }
}
