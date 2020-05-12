import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;
import parseranalyzer.LeftFacCFG;
import parseranalyzer.LeftRecCFG;

import java.util.List;

public class ParserAnalyzer {

    private final CFG cfg;

    public ParserAnalyzer(CFGRulesFile cfgRulesFile) throws Exception {
        this.cfg = new CFG(
                new LeftFacCFG(
                    new LeftRecCFG(cfgRulesFile)
                )
        );
    }

    public List<List<String>> parse(List<String> tokens) throws Exception {
        tokens.add("$");
        this.cfg.createFirstAndFollowSets();
        this.cfg.createLL1Table(this.cfg.getFirst(), this.cfg.getFollow());

        System.out.println(cfg.toString());
        return cfg.parse(tokens);
    }
}
