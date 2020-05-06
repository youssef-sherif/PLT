import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;
import parseranalyzer.LeftRecCFG;

import java.util.List;

public class ParserAnalyzer {

    private final CFG cfg;

    public ParserAnalyzer(CFGRulesFile cfgRulesFile) {
//        this.cfg = new CFG(cfgRulesFile.getCFGRules());
        this.cfg = new CFG(
                new LeftRecCFG(cfgRulesFile.getCFGRules())
        );
    }

    public void parse(List<String> tokens) throws Exception {
        tokens.add("$");
        this.cfg.createFirstAndFollowSets();
        this.cfg.createLL1Table(this.cfg.getFirst(), this.cfg.getFollow());

        System.out.println(cfg.toString());
        System.out.println(cfg.parse(tokens));
    }
}
